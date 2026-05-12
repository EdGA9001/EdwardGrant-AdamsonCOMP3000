from flask import Flask
import serial
import time
import threading

app = Flask(__name__)
ARDUINO_PORT = "COM7" #this may change
BAUD_RATE = 9600
current_value = "0"

def read_arduino():
    global current_value
    ser = serial.Serial(ARDUINO_PORT, BAUD_RATE, timeout=1)
    print(current_value)

    time.sleep(2)
    
    while True:
        if ser.in_waiting > 0:
            current_value = ser.readline().decode('utf-8').strip()
        time.sleep(0.5)

@app.route('/heartrate')
def get_heartrate():
    print(current_value)
    return current_value

if __name__ == '__main__':
    threading.Thread(target=read_arduino, daemon=True).start()
    app.run(host='0.0.0.0', port=8000)