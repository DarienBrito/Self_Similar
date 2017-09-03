# Self_Similar

_________________________________________________________________________

An implementation in SuperCollider of Tom Johnson's techniques, as described in the 
"Self-Similar Melodies" book
_________________________________________________________________________


## What is this?

This repository is about number series. Its purpose is simply to offer an insight into Tom Johnson's ideas on regard to pattern generation and organisation by means of the SuperCollider programming language.

My intention while writing was simply to understand better what I was reading in "Self-Similar Melodies" and to distract myself from the many urgent things that need to be done in my life, and that I keep procrastinating.

From this follows that this is not a paper on theory, or aesthetics, or mathematics or even programming, although directly or indirectly deals with these with more or less emphasis. This little manual was made just for fun, and hopefully will be read in the same way.

Try out every serie and feel free to bend it, twist it and/or to change it for your musical or non-musical needs. Keep in mind that these are numbers, and do not have to be used in the context of pitch, rhythm or music necessarily.

If you want to know who Tom Johnson is and what he does you can go to:
http://www.editions75.com

## About the implementation

There are basically two approaches that one may take when trying to code the sequences in the book:

1. To hard code them in arrays
2. To generate the numbers with logic

The first one is easy to accomplish with Array operations, while the second one can be approached economically with Patterns. Both approaches are combined here to accomplish self-similarity. I do not
follow a strict way of doing this, I've just settled down for solutions that are easily understandable and that work as intended. For this reason you may find that what I did in one of the series is not the same as in other, even if the problem is essentially the same.

Bare in mind that there are many ways to tackle the same problem. I offer here my own strategies, which may not be the best or most elegant solutions (you will not find recursive functions for instance,
although such solutions would shine in the context of this endeavour). If you have a better solution for a given problem please fork the project from GitHub and get in touch, so the code can get cleaner and better.

I have kept the aural realisation of the sequences, that is pitch, rhythm and sound timbre, as simple as possible (as boring as possible, in other words). This is because I wanted to have a standard definition for the sequences so its always clear what is going on. Remember that this is not about sound synthesis, composition or aesthetics, but just about integer number sequences. All the examples can be taken as far as you want, and the series can be used to anything that use numbers for execution.

## Note

Keep in mind that the numbers can represent an arbitrary parameter, that is anything you like. However, for the sake of simplicity, I'm relating the numbers to corresponding degrees in a Scale Class in SuperCollider, where 0 is the first degree, 1 the second degree, etc.

The snippet of the series on the other hand, is kept with 1 as the starting point instead of 0, so in case that you have the "Self Similar Melodies" book you can easily check what serie are you looking at. The sections and subsections of each part are kept with the titles of the book as well.

Look at the first example in “Counting 123” that contains a detailed explanation of the format I've chosen to show the procedures, and what each parameter represents.

* If you want to see who I am and what do I do you can find me at:  www.darienbrito.com 
* Or if you just want to say "hi" you can write me an e-mail to: darienbrito@icloud.com

______________________________________________________________________________
Darien Brito - Den Haag, Nederlands, 2015 


