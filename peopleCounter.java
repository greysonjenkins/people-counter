/************************************************************
BOILERPLATE CODE; still need to fill in placeholder arguments
************************************************************/

// Include libraries
#include "UbidotsESPMQTT.h"

// Define cosntants
#define TOKEN "..." // Ubidots TOKEN
#define WIFINAME "..." // SSID
#define WIFIPASS "..." // Wifi Password
#define DEVICE "pir-sensor" // Assign  device label
#define VARIABLE "motion" // Assign  variable label
#define LED 2
#define SENSOR D6

uint8_t counter=0;
unsigned long state = 0;

Ubidots client(TOKEN);

// Auxiliar functions
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i=0;i<length;i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

// Main functions
void setup() {
  // Setup code here, to run once:
  Serial.begin(115200);
  pinMode(SENSOR, INPUT);
  pinMode(LED, OUTPUT);
  client.wifiConnection(WIFINAME, WIFIPASS);
  client.begin(callback);
  }

void loop() {
  // Main code here, to run repeatedly:
  if (!client.connected()) {
      digitalWrite(LED, LOW);
      client.reconnect();
      digitalWrite(LED, HIGH);
  } else {
      digitalWrite(LED, HIGH);
  }

  uint8_t sensorValue = digitalRead(SENSOR);
  bool flag = false;

  if(sensorValue>0){
    for(uint8_t wait=0; wait<=4; wait++){
      sensorValue = digitalRead(SENSOR);
      Serial.println(sensorValue);
      if(sensorValue==1){
      counter++;
      }
      if(counter>3){
        flag = true;
      }
      delay(500);
    }
  }

  Serial.println("sending data");
  uint8_t value;
  if(flag){
    value = 1;
    client.add(VARIABLE, value);
    client.ubidotsPublish(DEVICE);
  }else{
    value = 0;
    if(state == 10000){
      client.add(VARIABLE, value);
      client.ubidotsPublish(DEVICE);
      state = 0;
    }
  }
  state++;
  client.loop();
  counter = 0;
}
