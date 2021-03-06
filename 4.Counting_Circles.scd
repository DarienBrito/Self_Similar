/*
- An implementation in SuperCollider of Tom Johnson's techniques, as described in the "Self-Similar Melodies" book -
by Darien Brito (2015)
http://www.darienbrito.com

This work is licensed under Creative Commons
Attribution-NonCommercial-ShareAlike 4.0 International Licence (CC BY-NC-SA 4.0)

To view a copy of this license go to:
http://creativecommons.org/licenses/by-nc-sa/4.0/
*/

/*_______________________________________________________________________

                          Counting in circles
_________________________________________________________________________*/

/*_______________________

Constants
_________________________*/

//Test sound:
(
SynthDef(\sine, {|freq = 440, amp = 0.1, rel = 0.8|
	var sig = SinOsc.ar(freq, 0, amp);
	var env = EnvGen.kr(Env.perc(releaseTime: rel),doneAction:2);
	sig = sig*env;
	Out.ar(0, sig!2)
}).add;

// Parameters
~scale = Scale.minor;
~amp = Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25}} );
)

/*___________________________________

Isorhythm with 2 engines
_____________________________________*/

e1 = 1 2 3 4 5 4 3
e2 = 4 5 4 3 2 1
e1 = 2 3 4 5 4 3 2
e2 = 3 4 5 4 3 2
e1 = 3 4 5 4 3 2 1
e2 = 2 3 4 5 4 3
(...)

// Hint: count 2 different series starting
// at different indexes and join them at the end

(
//Structure
var limit = 6;
var interval = 1;
var dur = 0.15;
//Calculate serie
var seq = Array.series(limit, 0, interval);
var temp, serbTemp, final;
var serA = List(), serB = List();
var groupLimit = 10;
var offset = 5;
var count = 0;
seq = seq.mirror1;
while({count < groupLimit}, {
	temp = 7.collect{|i|
		seq.wrapAt(i + count);
	};
	serbTemp = 6.collect{|i|
		seq.wrapAt((offset - i) + count);
	};
	serA.add(temp);
	serB.add(serbTemp);
	count = count + 1;
});
final = [serA,serB].lace(serA.size * 2);
final = final.flatten;
~sequence = List.new;

p = Pbindef(\circular1,
	\instrument, \sine,
	\degree, Pseq(final,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree)}),
	\dur, dur
).quant_(0).play;

)

/*___________________________________

Isorhythm with 2 engines (unlaced)
_____________________________________*/

e1 =
1 2 3 4 5
2 3 4 5 1
3 4 5 1 2
4 5 1 2 3
5 1 2 3 4

e2 =
4 3 2 1 5
3 2 1 5 4
(...)

(
//Structure
var limit = 5;
var interval = 1;
var dur = 0.15;
//Calculate serie
var seq = Array.series(limit, 0, interval);
var temp, serbTemp, final;
var serA = List(), serB = List();
var groupLimit = limit;
var offset = 3;
var count = 0;
while({count < groupLimit}, {
	temp = limit.collect{|i|
		seq.wrapAt(i + count)
	};
	serbTemp = limit.collect{|i|
		seq.wrapAt((offset - i) - count);
	};
	serA.add(temp);
	serB.add(serbTemp);
	count = count + 1;
});

// Unccomment the code below to do something different to what's on the book.
// I'm simply assigning a random interval to each value in the note (Repeated intervals may occur)
/*
r = Pxrand((-5, -4..5),limit).asStream.nextN(limit);
limit.do{|i|
	serA = serA.collect{|item| item.replace(i, r[i]);}
};
*/
final = serA++serB;
final = final.flatten;

// Or replace the values of the final sequence with the desired intervals
//y = [4,0,3,2,1];
//l = limit.collect{|i| final.replace(i,y[i])};
//l.postln;
~sequence = List.new;

p = Pbindef(\circular2,
	\instrument, \sine,
	\degree, Pseq(final,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

/*___________________________________

Isorhythm with 2 engines (laced)
_____________________________________*/

(
//Structure
var limit = 5;
var interval = 1;
var dur = 0.15;
//Calculate serie
var seq = Array.series(limit, 0, interval);
var temp, serbTemp, final;
var serA = List(), serB = List();
var groupLimit = limit;
var offset = 3;
var count = 0;
while({count < groupLimit}, {
	temp = limit.collect{|i|
		seq.wrapAt(i + count);
	};
	serbTemp = limit.collect{|i|
		seq.wrapAt((offset - i) - count);
	};
	serA.add(temp);
	serB.add(serbTemp);
	count = count + 1;
});

// Unccomment the code below to do something different to what's on the book.
// I'm simply assigning a random interval to each value in the note (Repeated intervals may occur)
// How do i do that? This doesn't work because is destructive. Maybe has to be done in a previous state.
/*
r = Pxrand((-5, -4..5),limit).asStream.nextN(limit);
limit.do{|i|
	serA = serA.collect{|item, j| item.replace(i, r[i]);}
};
*/
final = [serA,serB].lace(serA.size * 2);
final = final.flatten;
~sequence = List.new;

p = Pbindef(\circular2b,
	\instrument, \sine,
	\degree, Pseq(final,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

/*____________________

Isorythm for rythm
______________________*/

(
//Structure
var limit = 5;
var tempo = 120;
var binaryMap = 16;
//Calculate series
var seq = [0,5,4,6,3];
var rythm = 16.collect{|i| [1, 1/2, 1/4, 0.75, 1.5].wchoose([0.1,1,0.1,1,0.5].normalizeSum)}; //In the book's example is of length 16
~sequence = List();

p = Pbindef(\circular3,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25;} }),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, Pseq(rythm,inf),
	\stretch, 60/tempo
).quant_(0).play;
)

/*_______________________________________________

Binary assignment for rhythm (kind of isorythmic)
_________________________________________________*/

0 0 1 0 1 0 0 1 1 0 1 0 1 0 1 1 0 1 // Play only when 1

(
//Structure
var seq = [0,3,4,6,7]; // Circle 1
var dur = 0.15;
//Calculate series
var test = 16.collect{|i| if (0.5.coin){1}{0}}; // Circle 2
var deg = Pseq(seq,inf).asStream;
~sequence = List();

p = Pbindef(\circular4,
	\instrument, \sine,
	\scale, ~scale,
	\tester, Pseq(test,inf),
	\degree, Pfunc{|ev| if(ev.tester > 0) {deg.next} {\}},
	\octave, Pseq([4,5,6],inf), // Circle 3
	\dur, dur
).quant_(0).play;
)

/*_________________________

Isorythm with 3 engines
___________________________*/

// Main: [0,1,2,3,4]
0 //Circle 1 returns 1 element at a time
0 1 //Circle 2 returns 2 elements at a time
0 1 2 //Circle 3 returns 3 elements at a time
//They are then interlaced
0  01  012
1  23  234
2  41  123
3  23  234
4  41  123

(
//Structure
var limit = 5;
var dur = 0.15;
var factor = 1;
var ret1 = 1; //<--- Change these for new patterns
var ret2 = 2;
var ret3 = 3;
//Series
var seq = (0..limit).scramble * factor;
var circle1, circle2, circle3, final;
circle1 = Pser(seq,ret1,Pseries(0,ret1,inf).asStream);
circle2 = Pser(seq,ret2,Pseries(0,ret2,inf).asStream);
circle3 = Pser(seq,ret3,Pseries(0,ret3,inf).asStream);
final = Pseq([circle1,circle2,circle3],inf);
~sequence = List();

p = Pbindef(\circular6,
	\instrument, \sine,
	\degree, final,
	\scale, ~scale,
	\amp, Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25;} }),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)


/*_____________________________________

Isorythm generalization for N engines
_______________________________________*/

(
//Structure
var n = 4; // works nicely with even N's
var limit = 8;
var dur = 0.15;
var factor = 1;
//Series
var seq = (0..limit).scramble * factor;
var circles = n.collect{|i| Pser(seq,i+1, Pseries(0,i+1,inf).asStream)};
var final = Pseq(circles,inf);
~sequence = List();

p = Pbindef(\circular7,
	\instrument, \sine,
	\degree, final,
	\scale, ~scale,
	\amp, Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25;} }),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

/*____________________________________

Using two motors for specific beats
_______________________________________*/

(
//Structure
var limit = 8;
var tempo = 180;
var interval = 2;
var rt1 = 3;
var rt2 = 2;
//Series
var seqs;
var motor1 = {[1/2,1/4,1].wchoose([1, 0.5, 0.5].normalizeSum)} ! 8;
var motor2 = {[1/2,1].wchoose([0.5, 1].normalizeSum)} ! 4;
var seq1 = motor1.size.collect{|i| interval };
var seq2 = (motor2.size-1).collect{|i| 0 } ++ [\];
var circle1 = Pser(motor1,2,Pseries(0,2,inf).asStream);
var circle2 = Pser(motor2,2,Pseries(0,2,inf).asStream);
var circles = Pseq([circle1,circle2],inf);
seq1 = Pser(seq1,rt1,Pseries(0,rt1,inf).asStream);
seq2 = Pser(seq2,rt2,Pseries(0,rt2,inf).asStream);
seqs = Pseq([seq1,seq2],inf);

p = Pbindef(\circular8,
	\instrument, \sine,
	\degree, seqs,
	\scale, ~scale,
	\amp, Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25;} }),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, circles,
	\rel, 0.25, //<--- shorter to hear it better
	\stretch, 60/tempo
).quant_(0).play;
)
