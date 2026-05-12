void setup() {
  Serial.begin(9600);
}

void loop() {
  int value = analogRead(A0);
  int mapped = map(value, 5, -5, 50, -50);
  //Serial.println(value);
  Serial.println(mapped);
  delay(500);
}