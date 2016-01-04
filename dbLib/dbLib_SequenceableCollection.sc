/*_____________________________________________________________

dbLib [additions to SuperCollider]

< Binarize lists >
Copyright (C) <2015>

by Darien Brito
http://www.darienbrito.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

________________________________________________________________*/

+ SequenceableCollection {

	// Transform a sequenceable collection to only 1s or 0s

	binarize {
		^this.collect{|item| if(item > 0) {1} {0}}
	}

	binarizeInvert {
		var new = this.binarize;
		^new.collect{|item| if(item != 0) {0} {1}}
	}

}

