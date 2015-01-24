.5::second => dur T;
T - (now % T) => now;

float o1;
float o2;
float o3;
float o4;
float o5;

float n1;
float n2;
float n3;
float n4;
float n5;
float n6;
float n7;

261.626 => o1;
293.665 => o2;
311.127 => o3;
293.665 => o4;
220.000 => o5;

391.995 => n1;
349.228 => n2;
311.127 => n3;
293.665 => n4;
311.127 => n5;
293.665 => n6;


SqrOsc s => dac;

while(true){

     0.15 => s.gain;

     o1 => s.freq;
     0.25::second => now;
     o2 => s.freq;
     0.25::second => now;
     o3 => s.freq;
     0.25::second => now;
     o4 => s.freq;
     0.25::second => now;
     o5 => s.freq;
     0.25::second => now;
 
     n1 => s.freq;
     0.25::second => now;
     n2 => s.freq;
     0.25::second => now;
     n3 => s.freq;
     0.25::second => now;
     n4 => s.freq;
     0.25::second => now;
     n5 => s.freq;
     0.25::second => now;
     n6 => s.freq;
     0.25::second => now;
}
