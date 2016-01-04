/*_____________________________________________________________

dbLib [additions to SuperCollider]

by Darien Brito (2015)
http://www.darienbrito.com

This work is licensed under the Creative Commons
Attribution-NonCommercial-ShareAlike 4.0 International License.

To view a copy of this license go to:
http://creativecommons.org/licenses/by-nc-sa/4.0/

________________________________________________________________*/


+ SimpleNumber {

	fifth {
		^this * 1.5;
	}

	fourth {
		^this * 1.33;
	}

	third {
		^this * 1.25;
	}

	thirdMin {
		^this * 1.189; //About this
	}



}

