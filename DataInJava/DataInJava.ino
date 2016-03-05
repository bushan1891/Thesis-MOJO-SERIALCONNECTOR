#define LIGHT_PIN         0
#define POTENTIOMETER_PIN 1
#define BUTTON_1_PIN      2
#define BUTTON_2_PIN      3
#define BUTTON_3_PIN      4

#define LED_1     13
#define LED_2     12

char inputs   [20];
char oldInputs[20];
char L1=0;
char L2=0;

void setup() {
    Serial.begin(115200);
    
    pinMode(BUTTON_1_PIN, INPUT);
    pinMode(BUTTON_2_PIN, INPUT);
    pinMode(BUTTON_3_PIN, INPUT);
    
    pinMode(LED_1, OUTPUT);
    pinMode(LED_2, OUTPUT);
}

void getInputs(){   
    sprintf(inputs, "SS:%03X:%03X:%c%c%c",
        analogRead(LIGHT_PIN),
        analogRead(POTENTIOMETER_PIN),
        digitalRead(BUTTON_1_PIN)?'1':'0',
        digitalRead(BUTTON_2_PIN)?'1':'0',
        digitalRead(BUTTON_3_PIN)?'1':'0'
    );
}

void loop() {
   
    getInputs();
    if( strcmp(inputs, oldInputs) != 0){
        strcpy(oldInputs, inputs);
        Serial.println(inputs);
    }
    
    if(Serial.available()){
        int ind=0;
        char buff[5];
        while(Serial.available()){
            unsigned char c = Serial.read();
            buff[ind] = c;
            if(ind++ > 6) break;
        }
        buff[2]=0;
        if(strcmp(buff, "L1")==0){
            L1 = 1-L1;
            digitalWrite(LED_1, L1);
        }
        if(strcmp(buff, "L2")==0){
            L2 = 1-L2;
            digitalWrite(LED_2, L2);
        }        
    }
    
    delay(10);
}

