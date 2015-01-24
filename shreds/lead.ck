2::second => dur T;
T - (now % T) => now;


float o1;
float o2;
float o3;
float o4;
float o5;
float o6;
float o7;
float o8;

261.626 => o1;
293.665 => o2;
311.127 => o3;
261.626 => o4;
349.228 => o5;
311.127 => o6;
293.665 => o7;
311.127 => o8;

SqrOsc s => dac;
0.1 => s.gain;

while(true){
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
     o6 => s.freq;
     0.25::second => now;
     o7 => s.freq;
     0.25::second => now;
     o8 => s.freq;
     0.25::second => now;

}

