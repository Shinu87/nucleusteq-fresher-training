from fastapi import FastAPI

app = FastAPI(title="Doctor Appointment Booking System")


@app.get("/")
def health_check():
    return {"message": "Doctor Appointment Booking System API Running"}