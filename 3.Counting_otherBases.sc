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

                        Counting in other bases
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

Counting in other bases
_____________________________________*/

// A function to convert to any integer base N if  N > 1
(~intBase = { | n , base|
	var output = List.new;
	if (base > 1) {
	while {n != 0} {
		r =  n % base; // remainder
		n = (n / base).floor; //quotient
		output.add(r.asString);
	};
	output = output.reverse; //swap it
	output = output.join("");
	output.asInt;
	} {"Input base must be bigger than 1".warn}
})

(
// Structure
var limit = 8;
var interval = 3;
var dur = 0.15;
var base = 8; // The radix
// Base derived serie
var dec = Array.series(limit, 0, interval);
var baseSeq = limit.collect{ |i| ~intBase.value(i, base)};
var seq = [dec,baseSeq].lace;
~sequence = List.new;

p = Pbindef(\differentBase1,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq,inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

p.play;
p.stop;
~sequence.postln;

/*_______________________________________

Interlacing sequences with a base number
_________________________________________*/

a = 0 1 2 3 4 0 1 2 3 4 (...)
b = 0 0 0 0 0 1 1 1 1 1 (...)
r = 0 0 1 0 2 0 3 0 4 0 0 1 1 1 2 1 3 1 4 1 (...)

(
// Structure
var limit = 5;
var interval = 5;
var dur = 0.15;
var base = limit;
var mode = 'rightLeft'; //change to leftRight to flip the interlacing
// Make series
var totalSize = (limit* base);
var serA = Array.series(limit, 0, interval);
var serB = Array.series(limit, 0, interval);
var out1 = Pseq(serA,inf).asStream;
var out2 = Pstutter(limit, Pseq(serB,inf)).asStream;
var seq;
serA = serA ! base;
serA = serA.flatten;
serB = out2.nextN(totalSize);
// Two modes of reading
switch(mode, 'rightLeft',{seq = [serA,serB].lace}, 'leftRight', {seq = [serB, serA].lace });
~sequence = List.new;

p = Pbindef(\differentBase2,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq,inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	//\octave, Pseq([4,5,6,4],inf),
	\dur, dur
).quant_(0).play;
)

p.play;
p.stop;
~sequence.postln;

/*________________

Base-Accumulative
__________________*/

a = 0 0  0   0    0     01 01 01  (...)
b = 0 01 012 0123 01234 0  01 012 (...)
r = 0 0 0 01 0 012 0 0123 0 01234 01 0 01 01 01 012 (...)

(
// Structure
var limit = 5;
var interval = 1;
var dur = 0.2;
var base = limit; // Set to limit for symmetry, but can be other values.
var mode = 'leftRight';
// Make series
var ser = Array.series(limit, 0, interval);
var sequenceA = Pn(Pser(ser,Pseries(1,1).asStream),inf).asStream;
var sequenceB = Pn(Pser(ser,Pseries(1,1).asStream),inf).asStream;
var returnN = ~numberItems.value(ser.size);
var seq;
sequenceA = limit.collect{|i| sequenceA.nextN(i+1) ! base};
sequenceB = limit.collect{|i| sequenceB.nextN(i+1) } ! base;
sequenceA = sequenceA.flatten;
sequenceB = sequenceB.flatten;
// Set mode
switch(mode, 'rightLeft',{seq = [sequenceA,sequenceB].lace}, 'leftRight', {seq = [sequenceA, sequenceB].lace });
seq = seq.flatten;
~sequence = List.new;

p = Pbindef(\differentBase3,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq,inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur,
	\rel, 0.5 //Shorter to hear better
).quant_(0).play;
)

p.play;
p.stop;
~sequence.postln;

/*___________________

Interlaced stuttered
_____________________*/

1 2 3 4
1 4 2 4 3 4 4 4
1 4 4 2 4 4 3 4 4 4 4 4
1 4 4 4 2 4 4 4 3 4 4 4 4 4 4 4
(...)

(
// Structure
var limit = 12;
var interval = 1;
var dur = 0.15;
var restsCount = 3;
var tempo = 120;
// Create sequence
var seq = Array.series(limit, 0, interval);
var lastItem = seq.last;
var reps = 1 + (limit * (limit-1));
var engine,players;
var seqContainer = List.new;
seq.removeAt(seq.size-1);
engine = Pseq([Pseq(seq,1),Pn(lastItem, reps )],inf).asStream.nextN(limit**2) ! limit;
seqContainer = engine.collect{ |array, i|
	var slice = array.size - (i * limit);
	array = slice.collect{|i| array[i]}
};
seqContainer = seqContainer.collect{ |array, i|
	var size = array.size;
	var globalOffset = \ ! ((i*16) + i); //Rests for voice entrances
 	var rests = \ ! size; //
	var new = [array,rests].lace;
	new = new.replace([\], \ ! restsCount);
	new = globalOffset++new
};

players = limit.collect{|i|
	Pbind(\instrument, \sine,
		\scale, ~scale,
		\degree, Pseq(seqContainer[i],inf),
		\amp, ~amp,
		\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
		\dur, dur,
		\stretch, 60/tempo //pulse
	)
};

p = Ppar(players).play
)

/*___________________

Interlaced inverted
_____________________*/

1 8 1,2 8,7 1,2,3 8,7,6 1,2,3,4 8,7,6,5

// Function to get accumulative size
(
~getAccumulativeSize = {|n|
	var amount = n * ((n/2) + (1/2));
	amount
})
//Test
a.nextN(~getAccumulativeSize.value(8));

(
// Structure
var limit = 8;
var interval = 1;
var dur = 0.15;
var barDistance = 8; //In rests
var stepDiference = 0;
// Create sequence
var seq = Array.series(limit, 0, interval);
var inverted, rests, offset, final;
seq = Pn(Pser(seq,Pseries(1,1).asStream),inf).asStream;
seq = limit.collect{|i| seq.nextN(i+1)};
inverted = (limit-1) - seq;
offset = \ ! barDistance;
stepDiference = \ ! stepDiference;
rests = (inverted.size).collect{|i| \ ! (((limit-1) - i ))++offset };
seq = [seq,rests].lace.flatten;
inverted = [rests, inverted].lace.flatten;
inverted = stepDiference++inverted; //The distance between sequences
~sequence = List.new;

p = Ppar([
	Pbind(\instrument, \sine,
		\scale, ~scale,
		\degree, Pseq(seq,inf),
		\amp, ~amp,
		\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
		\dur, dur
	),
	Pbind(\instrument, \sine,
		\scale, ~scale,
		\degree, Pseq(inverted,inf),
		\amp, ~amp,
		\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
		\dur, dur
	)]
).play;
)

p.stop;
p.play;
~sequence.postln;


/*_______________________________

Calculating sequences by radixes
_________________________________*/

Numbers with step 4 and base 5 //(Tom Jhonson's' example in the book) goes as follows:
4 8  12 16 20 24 28 (...)
//equals, in base 5 to:
4 13 22 31 40 44 103 (...)

// He adds 0's to compensate for a regular stream of 4 note sequences:
0004 0013 0022 0031 0040 0044 0103

Here two simple functions that give us the right sequences with or without compensation:

/////////////////////////////////////////////////////////////
// Function to get a radix sequence without 0' compensation
/////////////////////////////////////////////////////////////
(
~seqRadixSimple = {|size, step, base|
	var sequence = List.new;
	var transform;
	size.do{ |i|
		// Note: the .base method is an addition I've made to the class Integer in the standard SC distribution
		transform = ( i + 1 * step ).base(base).asString;
		transform.do{|item| sequence.add(item)};
	};
	sequence
}
)
~seqRadixSimple.value(2, 1, 2, 4)

//////////////////////////////////////////////////////////////////////////////
// Function to get a radix sequence with 0's compensation for a N note stream
//////////////////////////////////////////////////////////////////////////////
(
~seqRadix = {|size, step, base, compensation|
	var sequence = List.new;
	var transform;
	size.do{ |i|
		var tempItem;
		transform = ( i + 1 * step).base(base).asString;
		tempItem = ((transform.size) - compensation).abs;
		tempItem =  0 ! tempItem;
		sequence.add(tempItem++transform)
	};
	sequence = sequence.flatten;
	sequence = sequence.collect{|item| if(class(item) == Integer) {item} {item.digit} } // Need to convert chars back to ints
}
)
~seqRadix.value(20, 4, 5, 4)

/*_______________________________

Sequence radix N
_________________________________*/

(
// Structure
var limit = 30;
var interval = 1;
var dur = 0.15;
var base = 8; //Play with
var step = 2; //these to get nice sequences
var compensation = 4; //To make N note sequences compensated with 0's
// Make sequence
var seq = ~seqRadix.value(limit, step, base, compensation);
seq = seq * interval;
~sequence = List.new;

p = Pbindef(\radixSeq1,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq,inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

p.play;
p.stop;
~sequence.postln;

/*_______________________________

Other ordering radix N
_________________________________*/

0033 0132 0231 0330 1023
1122 2212 2113 2020 1321 ...

(
// Structure
var limit = 30;
var interval = 1;
var dur = 0.15;
var base = 4; //Play with
var step = 15; //these to get nice sequences
var compensation = 4; //To make N note sequences compensated with 0's
// Get sequence
var seq = ~seqRadix.value(limit, step, base, compensation);
seq = seq * interval;
~sequence = List.new;

p = Pbindef(\radixSeq,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq,inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
//seq.postln;
)

~sequence.postln

/*_________________________________________________________________

Binary sequences with arbitrary initial positions for each number
___________________________________________________________________*/

1 = [ 4, , , , , 4, , , , , 4, , , ]
2 = [ , 6, , , , , 6, , , , , 6, , ]
3 = [ , , 8, , , , , 8, , , , , 8, ]
4 = [ , , , 4, , , , , 4, , , , , 4]

(
// Structure
var limit = 4;
var interval = 1;
var dur = 0.15;
var voices = 7;
var positions = [1,2,5,3,7,8,3]; //Starting positions for each voice in order;
var flip = true;
var base = 2;
var step = 1; //This remains at 1 so it's always a binary state
var compensation = 4; //To make N note sequences compensated with 0's
// Make sequence
var masterSeq = ~seqRadix.value(limit, step, base, compensation);
var arbitrary = ();
var restCount = Pn(\).asStream.nextN(voices-1);
restCount = restCount ! masterSeq.size;
if (flip) {masterSeq = (masterSeq - 1).abs}; //Flip interval direction
masterSeq = [masterSeq, restCount].lace;
masterSeq = masterSeq.flatten;
masterSeq = masterSeq * interval;
voices.do{|i|
	var offset = \ ! i;
	var currentVoice = offset ++ (masterSeq+positions[i]) ;
	currentVoice.postln;
	arbitrary.put(i,
		Pbind(\instrument, \sine,
			\scale, ~scale,
			\degree, Pseq(currentVoice,inf),
			\amp, ~amp,
			\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
			\dur, dur)
	);
};
Ppar(arbitrary.asList).play
)

/*____________________________

Encoding Base 2 number series
______________________________*/

  I      II        III        IV         V
binary  signs  occurrences  position  position2

0000    +      0            0         0
0001    -      1            1         1
0010    -      1           -1         2
0011    +      2            0         1
0100    -      1            1        -4

binary: the binary representation of the number (8 bits, here only 4 shown)
signs: '+' if the number of 1s is even '-' if odd
occurrences: shows the total number of 1s in each binary number
position: odd columns of the binary number equal to -1, even ones to +1 ones; shows the sum
position: columns (counting from last) go in series doubling their value and interchanging signs (1,-2,4,-8,etc...)

// Modified to produce radix series in binary arrays using the .asBinaryDigits method
(
~seqRadixCluster = {|size, step|
	var sequence = List.new;
	var transform;
	size.do{ |i|
		var tempItem;
		transform = (i * step).asBinaryDigits;
		sequence.add(transform)
	};
	sequence
})

//Test it:
x = ~seqRadixCluster.value(16,1)

(
var limit = 255;
var interval = 1;
var dur = 0.15;
var offset = 0;
// Master sequence
var masterSeq = ~seqRadixCluster.value(limit+1, 1);
var columns = ();
var signsList = List.new;
var occurencesOfOne = List.new;
var positionVal = List.new;
var positionVal2 = List.new;
var counter = 0;
//Occurences
masterSeq.do{|item|
	var occurence = item.occurrencesOf(1);
	if(occurence.even) {signsList.add('+')}
	{signsList.add('-')};
	//Occurences of 1
	occurencesOfOne.add(occurence);
} ;
//Positions value assignment 1 (in binary, 8 bits, the biggest possible value is 255)
masterSeq.do{|item|
	var counter = 0;
	if(item[item.size-1] == 1) {counter = counter + 1};
	if(item[item.size-3] == 1) {counter = counter + 1};
	if(item[item.size-5] == 1) {counter = counter + 1};
	if(item[item.size-7] == 1) {counter = counter + 1};
	if(item[item.size-2] == 1) {counter = counter - 1};
	if(item[item.size-4] == 1) {counter = counter - 1};
	if(item[item.size-6] == 1) {counter = counter - 1};
	if(item[item.size-8] == 1) {counter = counter - 1};
	positionVal.add(counter);
};
//Positions value assignment 2
masterSeq.do{|item|
	var counter = 0;
	if(item[item.size-1] == 1) {counter = counter - 1};
	if(item[item.size-2] == 1) {counter = counter + 2};
	if(item[item.size-3] == 1) {counter = counter - 4};
	if(item[item.size-4] == 1) {counter = counter + 8};
	if(item[item.size-5] == 1) {counter = counter - 16};
	if(item[item.size-6] == 1) {counter = counter + 32};
	if(item[item.size-7] == 1) {counter = counter - 64};
	if(item[item.size-8] == 1) {counter = counter + 128};
	positionVal2.add(counter);
};
columns.put(\sequence, masterSeq * interval + offset);
columns.put(\evenOnes, signsList);
columns.put(\occOfOne, occurencesOfOne * interval + offset);
columns.put(\position, positionVal * interval + offset);
columns.put(\position2, positionVal2 * interval + offset);
//Now we can use some of this series to sequence:

p = Pbindef(\columns,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(columns[\position],inf),
	//\degree, Pseq(columns[\position2],inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

p.play;
p.stop;
~sequence.postln;
