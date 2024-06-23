#include <WiFi.h>
#include <WebServer.h>
#include <HCSR04.h>
#include <ESP32Servo.h>

#define servoPin 2
// #define echoPin 32
// #define triggerPin 33
#define leftMotorFront 14
#define leftMotorBack 12
#define leftMotorPWM 13
#define rightMotorFront 15
#define rightMotorBack 27
#define rightMotorPWM 4
#define speed 150

const char *ssid = "HP 1293";
const char *password = "12345678";

WiFiServer server(80);
WiFiClient client;
IPAddress staticIP(192, 168, 1, 100);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);

bool restart=true;
char c;
String output;
String slash="/";

const byte e=33;
const byte t=32;

Servo scanServo;
UltraSonicDistanceSensor distanceSensor(t, e);
float distance;

void start();

void setup(){


}
int randomValue;

void loop(){

  if(restart){
    start();
    return;
  }

  client=server.available();

  if (client) {
    Serial.println("Client connected");

    while (client.connected()) {
      if (client.available()) {
        c = (char) client.read();
        Serial.print(c);
        switch(c){
          case 'w':
            motion(speed,speed);
            break;
          case 'a':
            motion(-speed,speed);
            break;
          case 's':
            motion(-speed,-speed);
            break;
          case 'd':
            motion(speed,-speed);
            break;
          case 't':
            motion(0,0);
            break;
          case 'p':
            for(int i=-1;i<91;i++){
              scanServo.write(getMappedAngle(i));
              // Serial.println(calculateDistance(t,e));
              distance=distanceSensor.measureDistanceCm();
              output=String(i)+slash+String(distance);
              client.println(output);
              Serial.println(output);
              delay(100);
            }
        }
      }
    }
  }  
}

void start(){
  Serial.begin(57600);
  WiFi.begin(ssid, password);
  WiFi.mode(WIFI_STA);

  Serial.print("Connecting to ");
  Serial.print(ssid);
  Serial.print(" with ");
  Serial.println(password);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println(".");
    delay(1000);
  }

  Serial.println("Connected to WiFi");
  Serial.println((WiFi.localIP().toString()));

  server.begin();

  
  pinMode(leftMotorFront,OUTPUT);
  pinMode(leftMotorBack, OUTPUT);
  pinMode(leftMotorPWM,OUTPUT);
  
  pinMode(rightMotorFront,OUTPUT);
  pinMode(rightMotorBack, OUTPUT);
  pinMode(rightMotorPWM,OUTPUT);

  pinMode(servoPin,OUTPUT);
  scanServo.attach(servoPin);
  restart=false;
}

void motion(int leftSpeed,int rightSpeed){
  digitalWrite(leftMotorFront, LOW);  
  digitalWrite(leftMotorBack, LOW);
  analogWrite(leftMotorPWM, 0);

  digitalWrite(rightMotorFront, LOW);  
  digitalWrite(rightMotorBack, LOW);
  analogWrite(rightMotorPWM, 0);

  if(leftSpeed<0){
    leftSpeed=-leftSpeed;
    digitalWrite(leftMotorBack, HIGH);
  }else{
    digitalWrite(leftMotorFront, HIGH);
  }

  if(rightSpeed<0){
    rightSpeed=-rightSpeed;
    digitalWrite(rightMotorBack, HIGH);
  }else{
    digitalWrite(rightMotorFront, HIGH);
  }

  analogWrite(leftMotorPWM, leftSpeed);
  analogWrite(rightMotorPWM, rightSpeed);

}

int getMappedAngle(int angle){
  return map(angle,0,90,90,0);
}