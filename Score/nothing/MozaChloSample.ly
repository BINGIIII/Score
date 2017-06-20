
\version "2.18.2"

\header {
    worknumber = "K. 524"
    copyright = "Copyright © 2002 Recordare LLC"
    title = "An Chloe (Page 1)"
    encodingdate = "2011-08-08"
    encodingsoftware = "Finale 2011 for Windows"
    composer = "Wolfgang Amadeus Mozart"
    poet = "Johann Georg Jacobi"
    }

#(set-global-staff-size 18.0675)
\paper {
    paper-width = 21.59\cm
    paper-height = 27.94\cm
    top-margin = 1.27\cm
    bottom-margin = 1.27\cm
    left-margin = 1.27\cm
    right-margin = 1.27\cm
    between-system-space = 2.06\cm
    page-top-space = 1.11\cm
    }
\layout {
    \context { \Score
        skipBars = ##t
        autoBeaming = ##f
        }
    }
    
PartPTwoVoiceOne =   {
    \clef "treble" \key es \major \time 2/2  }

PartPTwoVoiceThree =   {
    \clef "bass" \key es \major \time 2/2  }


% The score definition
\score {
    <<
       
        \new PianoStaff <<
            \set PianoStaff.instrumentName = "Piano"
            \context Staff = "1" << 
                \context Voice = "PartPTwoVoiceOne" { \PartPTwoVoiceOne }
                >> \context Staff = "2" <<
                \context Voice = "PartPTwoVoiceThree" { \PartPTwoVoiceThree }
                >>
            >>
        
        >>
    \layout {}
    % To create MIDI output, uncomment the following line:
    %  \midi {}
    }

