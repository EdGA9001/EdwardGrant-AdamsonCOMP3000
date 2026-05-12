int hrDataPin = A0;

void setup() {
    Serial.begin(9600);
}

void loop() {
    int value = analogRead(hrDataPin);
    Serial.println(value);
    delay(500);
}