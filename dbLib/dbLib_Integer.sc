/*_____________________________________________________________

dbLib [additions to SuperCollider]

by Darien Brito (2015)
http://www.darienbrito.com

This work is licensed under the Creative Commons
Attribution-NonCommercial-ShareAlike 4.0 International License.

To view a copy of this license go to:
http://creativecommons.org/licenses/by-nc-sa/4.0/

________________________________________________________________*/

+ Integer {

	// Transform an int to base N
	base { |base|
		var num = this, remainder;
		var output = List.new;
		if (base > 1) {
			while {num!=0} {
				remainder = num % base;
				num = (num/base).floor;
				output.add(remainder.asString);
			};
			output = output.reverse;
			output = output.join("");
			^output.asInt;
		} {"Base has to be > than 1".warn; ^nil}
	}
}
