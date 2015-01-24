.5::second => dur T;
T - (now % T) => now;


float o1;
float o2;
float o3;
float o4;
float o5;
float o6;
float o7;
float o8;

311.127 => o1;
349.228 => o2;
261.626 => o3;
195.998 => o4;
311.127 => o5;
349.228 => o6;
233.082 => o7;
207.652 => o8;

SqrOsc s => Gain p => dac;
0.1 => p.gain;

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

