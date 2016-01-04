/*
- An implementation in SuperCollider of Tom Johnson's techniques, as described in the "Self-Similar Melodies" book -
by Darien Brito (2015)
http://www.darienbrito.com

This work is licensed under Creative Commons
Attribution-NonCommercial-ShareAlike 4.0 International Licence (CC BY-NC-SA 4.0)

To view a copy of this license go to:
http://creativecommons.org/licenses/by-nc-sa/4.0/
*/


/*_________________________________________________

                   Counting 1 2 3
___________________________________________________*/


/*_______________________

Constants for our tests
_________________________*/

// I use this instead of the SC default Patterns sound because I dislike the default sound very much :)
(
SynthDef(\sine, {|freq = 440, amp = 0.1, rel = 0.8|
	var sig = SinOsc.ar(freq, 0, amp);
	var env = EnvGen.kr(Env.perc(releaseTime: rel),doneAction:2);
	sig = sig*env;
	Out.ar(0, sig!2)
}).add;

// I chose the good old-fashioned minor scale, but you can change it to anything you like:
~scale = Scale.minor;
// This function compensates for higher pitches being perceived psicoacoustically as louder:
~amp = Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25}} );
)

//Test
Synth(\sine)


/*_______________________

Simple counting:            //<---The labeling of the serie
_________________________*/

1
2   //<---The snippet of the serie
3
(...)

(
// Structure (flexible parameters for you to try out):
var limit = 10; // The maximum number count
var dur = 0.15; // The duration between the notes
var interval = 1; // The interval for the note sequence

// Make sequence:
var count = Array.series(limit, 0, interval);

// A container (if you want to retrieve the sequence):
~sequence = List.new;

p = Pbindef(\counting, //Pbindef so you can change it on the fly to something else
	\instrument, \sine, //The instrument chosen in "Constants"
	\scale, ~scale, //The scale chosen in "Constants"
	\degree, Pseq(count, inf), // The sequence is applied to "degree" of the scale
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // The function to compensate for higher frequencies perceived as louder defined in "Constants"
	\dur, dur // The local duration
).quant_(0).play // Quantization of 0 so it starts immediately
)
p.pause
p.resume
~sequence.postln; // Retrieve played serie

/*_______________________

Accumulative
_________________________*/

1
12
123
(...)

// This can be achived with the .pyramid(1) method, nevertheless I'm using methodsthat are meant to illustrate the process clearly.

(
// Structure
var limit = 10;
var dur = 0.15;
var interval = 1;
// Make sequence
var seq = List();
limit.do{|i| i.do{|j| seq.add(j)}; seq.add(i)};
~sequence = List.new;
p = Pbindef(\counting2,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\amp, ~amp,
	\dur, dur
).quant_(0).play
)
p.pause
p.resume
~sequence;

/*_______________________

Repetitive-accumulative
_________________________*/

1
1 22
1 22 333
(...)

(
// Structure
var limit = 10;
var dur = 0.15;
var interval = 1;
// Make sequence
var seq = List();
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(j) }}};
~sequence = List.new;
p = Pbindef(\counting3,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\amp, ~amp,
	\dur, dur
).quant_(0).play
)
p.pause
p.resume
~sequence;

/*_______________________

Additive-repetitive
_________________________*/

1
12 12
123 123 123
1234 1234 1234 1234
(...)

(
// Structure
var limit = 4;
var dur =  0.15;
var interval = 2;
//Make sequence
var seq = Array.series(limit, 0, interval);
var counter = Pseries(1, 1, inf);
var numStream = Pstutter(counter, counter).asStream;
var repeatStream = Pstutter(counter, counter).asStream;
~sequence = List.new;

p = Pbindef(\counting5,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pn(Pn(Pser(seq, numStream),repeatStream),inf),
	\recorder, Pfunc({ |ev| ~container.add(ev.degree)}),
	\amp, ~amp,
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;



/*_______________________

Palindromes
_________________________*/

/////////
// Tip
/////////

// Palindromic reading of an array can be done like this with patterns
//(the array has to be hard-coded):
a = [1,2,3];
q =  Pwalk(a, 1, Pseq([1,-1],inf)).asStream;
q.nextN(5)

// But it is easier to simply use the .mirror method instead:
k = Pseq(a.mirror,inf).asStream;
k.nextN(5)

/*_______________________

Simple palindrome
_________________________*/

1
2
3
2
1

(
// Structure
var limit = 6;
var dur =  0.15;
var interval = 4;
//Make sequence
var seq = Array.series(limit, 0, interval).mirror;
var counter = Pseries(1,1);
~sequence = List.new;
p = Pbindef(\palindrome1,
	\instrument, \sine,
	\degree, Pseq(seq, inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*___________________________________________________

Palindrome, accumulative with palindromic series
_____________________________________________________*/

1
1 2 1
1 2 3 2 1
1 2 1
1

(
//Structure
var dur = 0.15;
var limit = 10;
var interval = 3;
//Make sequence
var numItems = Pseries(1,1,inf).asStream;
var return = Pn(Pser(Array.series(limit,0,interval), numItems)).asStream;
var seq = List.new;
//Get palindromes
limit.do{ |i| seq.add(return.nextN(i+1).mirror)};
//Make a palindromic set of palindromes
seq = seq.mirror;
seq = seq.flatten;
~sequence = List.new;

p = Pbindef(\palindrome3,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*___________________________________________________

Palindrome, non-palindromic accumulative series
_____________________________________________________*/

1
1 12
1 12 123
1 12
1

(
//Structure
var dur = 0.15;
var limit = 5;
var interval = 2;
// Make the palindromes
var numItems = Pseries(1,1,inf).asStream;
var array = Array.series(limit,0,interval);
var return = Pn(Pser(array, numItems)).asStream;
var seq = List.new;
var acc, accSeq;
// Get the palindromes
limit.do{ |i| seq.add(return.nextN(i+1).mirror)};
// Create accumulative serie
acc = Pn(Pser(seq, Pseries(1,1,inf).asStream), seq.size).asStream;
accSeq = acc.nextN(seq.size * 2);
accSeq = accSeq.mirror;
accSeq = accSeq.flatten;
~sequence = List.new;

p = Pbindef(\palindrome4,
	\instrument, \sine,
	\degree, Pseq(accSeq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*______________________________________________________

Palindrome, accumulative-accumulative palindromic series
________________________________________________________*/

1
1 121 1
1 121 12321 121 1
1 121 1
1

(
//Structure
var limit = 5;
var dur = 0.1;
var interval = 2;
// Make palindromes
var numItems = Pseries(1,1,inf).asStream;
var array = Array.series(limit,0,interval);
var return = Pn(Pser(array, numItems)).asStream;
var return2;
var seq = List.new; // For level 1
var finalSeq = List.new; // For level 2
// Get the palindromes
limit.do{ |i| seq.add(return.nextN(i+1).mirror)};
// Make a palindromic set of palindromes
seq = seq.mirror;
// One more level of palindromic iteration
return2 = Pn(Pser(seq,Pseries(1,1).asStream)).asStream;
finalSeq = List.new;
limit.do{ |i| finalSeq.add(return2.nextN(i+1).mirror)};
//Make the macro palindrome
finalSeq = finalSeq.mirror;
// We need to double flatten them since we have 2 layers
2.do{finalSeq = finalSeq.flatten};
~sequence = List.new;

p = Pbindef(\palindrome5,
	\instrument, \sine,
	\degree, Pseq(finalSeq,inf),
	\scale, ~scale,
	\octave, Pseq([4,5],inf), // Varying here the octavation
	\amp, ~amp,
	\debugger, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*____________________________

Palindrome, simple-repetitive
______________________________*/

1
22
333
22
1

(
// Structure
var limit = 5;
var interval = 1;
var dur = 0.15;
// Make sequence
var counter = Pseries(1,1);
var stutter = Pstutter(counter, Pseries(1,1)).asStream;
var seq = limit.collect{ |i| stutter.nextN(i+1).postln };
seq = seq.mirror;
seq = seq.flatten;
seq = seq * interval;
~sequence = List.new;

p = Pbindef(\palindrome7,
	\instrument, \sine,
	\scale, ~scale,
	\degree,Pseq(seq,inf),
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur,
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*______________________________________________________

Palindrome, repetitive-accumulative mirrored series
________________________________________________________*/

1
12221
122333333221
12221
1

(
//Structure
var limit = 5;
var interval = 2;
var dur = 0.15;
// Make the palindromes
var numItems = Pseries(1,1,inf).asStream;
var array = Array.series(limit, 0, interval);
var return = Pn(Pser( Array.series(limit,0,interval) , numItems)).asStream;
var seq = List.new;
var macroSeq = List.new;
var counter = Pseries(1,1,inf).asStream;
// Get the palindromes
limit.do{ |i| seq.add(return.nextN(i+1))};
// Scan and fabricate stuttered palindromes
seq.do{ |i|
	n = (i.size);
	v = Pstutter(Pseries(1,1), Pseq(i)).asStream;
	x = n * ((n/2)+ (1/2)); //* Appendix 1. See the appendix section to know where does this formula come from
	macroSeq.add(v.nextN(x).mirror);
};
macroSeq = macroSeq.mirror;
macroSeq = macroSeq.flatten;
~sequence = List.new;

p = Pbindef(\palindrome8,
	\instrument, \sine,
	\degree, Pseq(macroSeq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*_________________________________

Palindrome, accumulative repetitive
___________________________________*/

1
121 121
12321 12321 12321
121 121
1

(
//Structure
var limit = 4;
var dur = 0.15;
var interval = 2;
//Make series
var lookUp = Array.series(limit,0,interval);
var counter = Pseries(1,1).asStream;
var scan = Pn(Pser(lookUp,counter),limit).asStream;
var seq = limit.collect{ |i| Pstutter(Pseries(1,1).asStream, scan.nextN(i+1).mirror).asStream.nextN(i+1) };
seq = seq.mirror;
//We need to double flatten since we have nested arrays
2.do{seq = seq.flatten};
~sequence = List.new;

p = Pbindef(\palindrome9,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*_________________________________

Palindrome, incremental palindromic
___________________________________*/

1
232
45654
789 '10' 987
45654
232
1

(
//Structure
var dur = 0.15;
var limit = 4;
var interval = 3;
//Make series
var counter = Pseries(1,1).asStream;
var seq = limit.collect{ |i| counter.nextN(i+1).mirror};
seq = seq.mirror;
seq = seq.flatten;
seq = seq * interval;
~sequence = List.new;

p = Pbindef(\palindrome10,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

// Odd case

/*___________________________________________________

Non-palindrome, accumulative with palindromic series
_____________________________________________________*/

1
1 2 1
1 2 3 2 1
1 2 3 4 3 2 1
(...)

(
//Structure
var limit = 5;
var dur = 0.15;
var interval = 1;
//Make sequence
var numItems = Pseries(1,1,inf).asStream;
var return = Pn(Pser(Array.series(limit,0,interval), numItems)).asStream;
var seq = List.new;
limit.do{ |i| seq.add(return.nextN(i+1).mirror)};
seq = seq.flatten;
~sequence = List.new;

p = Pbindef(\palindrome2,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

/*______________________________________________________

Non palindrome, repetitive-unfolding palindromic series
________________________________________________________*/

1
12
122
1223
12233
122333
1223334
12233344
(...)

// --- VERSION 1 ---
(
//Structure
var limit = 5; //is the last element relative to the intervalic content:
var dur = 0.15;
var interval = 1;
//Make sequence
var lookUp = Array.series(limit,0,interval);
var counter = Pseries(1,1).asStream;
//Size formula:
var size = limit * ((limit/2) + (1/2)).asInt;
var seq = size.collect{ |i|
	Pstutter(Pseries(1,1),Pseq(Pser(lookUp,counter).asStream.nextN(i+1),10)).asStream.nextN(i+1)
};
seq = seq.flatten;
~sequence = List.new;

p = Pbindef(\palindromic1,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\amp, ~amp,
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;

// --- VERSION 2 ---
(
//Structure
var limit = 6; //"Limit" is the total number of degrees returned
var dur = 0.15;
var interval = 3;
//Make series
var lookUp = Array.series(limit,0,interval);
var counter = Pseries(1,1).asStream;
var seq = limit.collect{ |i|
	Pstutter(Pseries(1,1),Pseq(Pser(lookUp,counter).asStream.nextN(i+1),10)).asStream.nextN(i+1)};
seq = seq.flatten;
~sequence = List.new;

p = Pbindef(\palindromic2,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\amp, ~amp,
	\octave, Pseq([4,5],inf),
	\dur, dur
).quant_(0).play;
)
p.pause
p.resume
~sequence;


/*____________

 APPENDIX 1.
____________*/

// Assuming we have the following serie:
a = [1, 2, 2, 3, 3, 3, 4, 4, 4, 4]

// This formula by A. Astudillo will find the value of the number in the given index for the array (starting from 1):
(
n = 3;
x = round(sqrt(2*n));
("The index "++n++ " in the serie corresponds to the number: ").postln;
$\t++x
)

// Now, this is my formula to calculate the final size of the serie of a given array:
(
n = 12;
x = n * ((n/2) + (1/2));
("The size of an array with "++n++ " items will be: ").postln;
$\t++x
)

// If you are curious, this is how I derived it:

{1} = 1 = {1}
 |---------|
{2} = 1 2 2 = {3}
 |-------------|
{3} = 1 2 2 3 3 3 = {6}
 |-------------------|
{4} = 1 2 2 3 3 3 4 4 4 4 = {10}
 |----------------------------|
{5} = 1 2 2 3 3 3 4 4 4 4 5 5 5 5 5 = {15}
 |--------------------------------------|
{6} = 1 2 2 3 3 3 4 4 4 4 5 5 5 5 5 6 6 6 6 6 6 = {21}
 |--------------------------------------------------|

21 / 6 = 3.5
15 / 5 = 3
10 / 4 = 2.5
6 / 3  = 2
3 / 2  = 1.5
1 / 1  = 1

// We can see that the growth per item is a constant of 0.5, so we can use this number to determine the growth.
