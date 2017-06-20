


PartPOneVoiceThree =  \relative a, {
    \clef "bass" \key a \major \grace { a32 ( [ cis32 e32 ] } a8 ) [ a8
    a8 a8 ] s2 | % 2
    \grace { a,32 ( [ cis32 e32 ] } a8 ) [ a8 a8 a8 ] s2 | % 3
    \grace { d,,32 ( [ fis32 a32 ] } d8 ) [ d8 d8 d8 ] s2 | % 4
    \grace { a32 ( [ cis32 e32 ] } a8 ) [ a8 a8 a8 ] s2 | % 5
    \grace { e,32 ( [ gis32 b32 ] } e8 ) [ e8 e8 e8 ] }

PartPOneVoiceTwo =  \relative cis'' {
    \clef "treble" \key a \major <cis e a>4 \arpeggio \arpeggio
    \arpeggio s2. s1 s1 s1 | % 5
    <e gis>2 }


% The score definition
\score {
    <<
        \new PianoStaff <<
            \set PianoStaff.instrumentName = "Piano"
            \context Staff = "1" << 
                \context Voice = "PartPzero" {\Partzero }
                >> \context Staff = "2" <<
                \context Voice = "Partone" { \Partone }
                >>
            >>
        
        >>
    \layout {}
    % To create MIDI output, uncomment the following line:
    \midi {}
    }

