"""
Email sending helper functions
"""

from functools import lru_cache

from fastapi_mail import ConnectionConfig, FastMail, MessageSchema, MessageType

from backend.config import get_settings


@lru_cache
def get_fast_mail() -> FastMail:
    settings = get_settings()

    mail_config = ConnectionConfig(
        MAIL_USERNAME=settings.smtp_username,
        MAIL_PASSWORD=settings.smtp_password,
        MAIL_FROM=settings.smtp_from,
        MAIL_PORT=settings.smtp_port,
        MAIL_SERVER=settings.smtp_server,
        MAIL_STARTTLS=settings.smtp_starttls,
        MAIL_SSL_TLS=settings.smtp_ssl_tls,
        USE_CREDENTIALS=True,
        VALIDATE_CERTS=True,
    )

    return FastMail(mail_config)


def build_setup_password_email_html(setup_link: str) -> str:
    return f"""
    <html>
        <body style="font-family: Arial, sans-serif; color: #333333;">
            <h2>Congratulations!</h2>
            <p>Your doctor account has been approved.</p>
            <p>Click the button below to create your password.</p>
            <p>
                <a href="{setup_link}"
                   style="background-color: #2e7d32; color: #ffffff; padding: 10px 20px;
                          text-decoration: none; border-radius: 5px; display: inline-block;">
                    Set Password
                </a>
            </p>
            <p>{setup_link}</p>
            <p>This link expires in 24 hours.</p>
        </body>
    </html>
    """


async def send_email(recipient_email: str, subject: str, html_body: str) -> None:
    message = MessageSchema(
        subject=subject,
        recipients=[recipient_email],
        body=html_body,
        subtype=MessageType.html,
    )
    fast_mail = get_fast_mail()
    await fast_mail.send_message(message)