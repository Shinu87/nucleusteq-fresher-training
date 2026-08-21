"""
How to survive a zombie apocalypse.

This script basically runs the daily routine for a bunker 
so we don't get eaten while we're sleeping.

How it works:
    Check the perimeter -> See how bad it is -> Pick a plan
    -> Either fight or hide -> Fix wounds -> Talk on the radio
"""

from datetime import datetime, timedelta
import logging
import random

from airflow import DAG
from airflow.operators.python import (
    PythonOperator,
    BranchPythonOperator,
)
from airflow.operators.bash import BashOperator
from airflow.utils.trigger_rule import TriggerRule


# General setup stuff

DAG_ID = "zombie_survival_dag"

logger = logging.getLogger(__name__)

DEFAULT_ARGS = {
    "owner": "survivor_team",
    "depends_on_past": False,
    "retries": 1,
    "retry_delay": timedelta(minutes=2),
}


# The stuff we need to do

def dawn_patrol(**context):
    """
    Walk around the fence in the morning.

    We pick a random number to see how many zombies are out there.
    """

    logger.info("Starting dawn perimeter patrol.")

    # Picking a random number for zombies.
    threat_score = random.randint(0, 100)

    logger.info(
        "Perimeter patrol completed. Threat score detected: %d/100",
        threat_score,
    )

    if threat_score >= 70:
        logger.warning(
            "HIGH THREAT detected. Zombie activity is significant."
        )
    elif threat_score >= 40:
        logger.warning(
            "MODERATE THREAT detected. Survivors should remain alert."
        )
    else:
        logger.info(
            "LOW THREAT detected. Perimeter appears relatively safe."
        )

    # Saving the score for later.
    context["ti"].xcom_push(
        key="threat_score",
        value=threat_score,
    )

    logger.debug(
        "Threat score %d stored in XCom with key 'threat_score'.",
        threat_score,
    )


def assess_threat(**context):
    """
    Turn that random number into a clear 'danger level'.
    """

    ti = context["ti"]

    threat_score = ti.xcom_pull(
        task_ids="dawn_patrol",
        key="threat_score",
    )

    if threat_score is None:
        logger.critical(
            "Threat score could not be retrieved from XCom."
        )
        raise ValueError("Missing threat score.")

    if threat_score >= 70:
        threat_level = "HIGH"
    elif threat_score >= 40:
        threat_level = "MEDIUM"
    else:
        threat_level = "LOW"

    logger.info(
        "Threat assessment completed. Score=%d, Level=%s.",
        threat_score,
        threat_level,
    )

    ti.xcom_push(
        key="threat_level",
        value=threat_level,
    )

    logger.debug(
        "Threat level '%s' stored in XCom.",
        threat_level,
    )


def choose_response(**context):
    """
    Should we go out and fight or just stay inside and hide?
    """

    ti = context["ti"]

    threat_level = ti.xcom_pull(
        task_ids="assess_threat",
        key="threat_level",
    )

    logger.info(
        "Choosing survival response for threat level: %s",
        threat_level,
    )

    if threat_level == "HIGH":
        logger.warning(
            "HIGH threat detected. Survivors will engage the horde."
        )
        return "engage_horde"

    logger.info(
        "Threat level is %s. Engaging is unnecessary.",
        threat_level,
    )
    logger.info(
        "The engage task will be skipped. Survivors will hide "
        "and secure the bunker."
    )

    return "hide_and_secure"


def medical_check(**context):
    """
    Check if anyone got bitten or hurt.
    """

    logger.info("Starting survivor medical check.")

    # See if anyone is bleeding.
    injured_survivors = random.randint(0, 2)

    if injured_survivors > 0:
        logger.warning(
            "Medical check found %d injured survivor(s).",
            injured_survivors,
        )
    else:
        logger.info(
            "Medical check completed. No injuries reported."
        )

    context["ti"].xcom_push(
        key="injured_survivors",
        value=injured_survivors,
    )

    logger.debug(
        "Medical result stored in XCom: %d injured survivor(s).",
        injured_survivors,
    )


def radio_check(**context):
    """
    Tell the other bunkers that we're still alive.
    """

    ti = context["ti"]

    threat_level = ti.xcom_pull(
        task_ids="assess_threat",
        key="threat_level",
    )

    injured_survivors = ti.xcom_pull(
        task_ids="medical_check",
        key="injured_survivors",
    )

    logger.info("Starting radio check-in with survivor groups.")

    logger.info(
        "STATUS REPORT | Threat=%s | Injured survivors=%s",
        threat_level,
        injured_survivors,
    )

    logger.info(
        "Survival routine completed successfully."
    )


# Making the actual DAG

with DAG(
    dag_id=DAG_ID,
    description="Automated zombie apocalypse survival routine.",
    default_args=DEFAULT_ARGS,

    # We need to do this every single morning at 6 AM.
    # This runs every day at 06:00.
    schedule="0 6 * * *",

    start_date=datetime(2026, 8, 1),
    catchup=False,
    max_active_runs=1,
    tags=["survival", "zombie", "training"],
) as dag:

    dawn_patrol = PythonOperator(
        task_id="dawn_patrol",
        python_callable=dawn_patrol,
    )

    assess_threat = PythonOperator(
        task_id="assess_threat",
        python_callable=assess_threat,
    )

    choose_response = BranchPythonOperator(
        task_id="choose_response",
        python_callable=choose_response,
    )

    engage_horde = BashOperator(
        task_id="engage_horde",
        bash_command=(
            "echo 'CRITICAL: Horde detected.' && "
            "echo 'Survivors are engaging the threat.' && "
            "echo 'Perimeter defense protocol activated.'"
        ),
    )

    hide_and_secure = BashOperator(
        task_id="hide_and_secure",
        bash_command=(
            "echo 'No critical threat detected.' && "
            "echo 'Bunker lockdown initiated.' && "
            "echo 'Lights and external signals disabled.'"
        ),
    )

    medical_check = PythonOperator(
        task_id="medical_check",
        python_callable=medical_check,
        trigger_rule=TriggerRule.NONE_FAILED_MIN_ONE_SUCCESS,
    )

    radio_check = PythonOperator(
        task_id="radio_check",
        python_callable=radio_check,
    )

    # How one task leads to another

    dawn_patrol >> assess_threat >> choose_response

    choose_response >> engage_horde
    choose_response >> hide_and_secure

    [engage_horde, hide_and_secure] >> medical_check

    medical_check >> radio_check
