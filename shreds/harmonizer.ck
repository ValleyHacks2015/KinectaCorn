2::second => dur T;
T - (now % T) => now;

OscRecv orec;
1234 => orec.port;
orec.listen();

orec.event("conductor/downbeat, s") @=> OscEvent myDownbeat;

string control;

int one;
int two;

while(true){
	
	myDownbeat => now;

	while( myDownbeat.nextMsg() != 0 )
	{
		myDownbeat.getString() => control;
	}
	
	if(control == "1"){
	Machine.remove( two );
	Machine.add("/ext_storage/KinectaCorn/shreds/harmony1.ck") => one;
	}

	if(control == "2"){
	Machine.remove( one );
	Machine.add("/ext_storage/KinectaCorn/shreds/harmony2.ck") => two;
        }
}
