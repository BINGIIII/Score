
\version "2.18.2"
% automatically converted by musicxml2ly from C:\Users\Bing\git\score\Score\SchbAvMaSample.xml

\header {
    worknumber = "D. 839"
    copyright = "Copyright © 2002 Recordare LLC"
    title = "Ave Maria (Ellen's Gesang III) - Page 1"
    encodingdate = "2011-08-08"
    encodingsoftware = "Finale 2011 for Windows"
    composer = "Franz Schubert"
    poet = "Walter Scott"
    }

#(set-global-staff-size 18.0675)
\paper {
    paper-width = 21.59\cm
    paper-height = 27.94\cm
    top-margin = 1.27\cm
    bottom-margin = 1.27\cm
    left-margin = 1.27\cm
    right-margin = 1.27\cm
    between-system-space = 2.02\cm
    page-top-space = 2.02\cm
    }
\layout {
    \context { \Score
        skipBars = ##t
        autoBeaming = ##f
        }
    }
PartPOneVoiceOne =  \relative bes' {
    \clef "treble" \key bes \major \time 4/4 R1*2 \break \repeat volta 2
    {
        | % 3
        bes4. a16 [ bes16 ] d4.. ( c16 ) | % 4
        bes4 r4 c4 ( \grace { d32 [ c32 ] } bes16 [ a16 ) g16 ( a16 ) ]
        \break | % 5
        bes4 r8 d8 d8. [ c32 ( bes32 ) ] a16 [ g16 ] d'16 [ e16 ] | % 6
        d4 cis8. [ a16 ] c8. [ bes16 ] \once \override TupletBracket
        #'stencil = ##f
        \times 2/3  {
            a16 ( [ c16 ) d16 }
        \once \override TupletBracket #'stencil = ##f
        \times 2/3  {
            es16 ( c16 ) a16 ] }
        \break | % 7
        bes4. d16 ( [ c16 ) ] c8. [ a16 ] \once \override TupletBracket
        #'stencil = ##f
        \times 2/3  {
            g16 ( [ b16 ) d16 ] }
        \once \override TupletBracket #'stencil = ##f
        \times 2/3  {
            f16 ( [ d16 ) b16 ] }
        | % 8
        c4 ( ~ \once \override TupletBracket #'stencil = ##f
        \times 4/6  {
            c16 [ g16 a16 bes16 \grace { c16*3/2 [ bes16*3/2 ] } a16 g16
            ) ] }
        f4 r8 f8 }
    }

PartPOneVoiceOneLyricsOne =  \lyricmode { A -- ve ri -- "a!" Jung --
    "mild!" Er -- hö -- ei -- Jung -- Fle -- "hen!" die -- Fel --
    "wild," soll mein "bet " __ "dir " __ we -- "hen." Wir }
PartPOneVoiceOneLyricsTwo =  \lyricmode { A -- ve ri -- "a!" Un --
    "fleckt!" Wenn wir die -- Fels sin -- ken "Schlaf," uns "deckt,"
    wird weich har -- Fels dün -- "ken." Du }
PartPOneVoiceOneLyricsThree =  \lyricmode { A -- ve ri -- "a!" Rei --
    "Magd!" Der Er -- und Luft mo -- "nen," dei -- Au -- "jagt," "sie "
    __ kön -- hier bei woh -- "nen!" Wir }
PartPTwoVoiceOne =  \relative d' {
    \clef "treble" \key bes \major \time 4/4 | % 1
    \once \override TupletBracket #'stencil = ##f
    \times 4/6  {
        r16 ^\markup{ \bold {Sehr langsam} } \pp <d f>16 ( _. [ <f bes>16
        _. <bes d>16 _> _. <f bes>16 _. <d f>16 ) _. ] }
    \once \override TupletBracket #'stencil = ##f
    \times 4/6  {
        r16 <d f>16 ( _. [ <f bes>16 _. <bes d>16 _> _. <f bes>16 _. <d
            f>16 ) _. ] }
    \once \override TupletBracket #'stencil = ##f
    \times 4/6  {
        r16 \< <f bes>16 ( ^. [ <bes d>16 ^. <d f>16 ^> ^. <bes d>16 ^.
        <f bes>16 ) ^. ] }
    \once \override TupletBracket #'stencil = ##f
    \times 4/6  {
        r16 <as d>16 ( ^. [ <d f>16 ^. <f as>16 ^> ^. <d f>16 ^. <as d>16
        \! ) ^. ] }
    | % 2
    \once \override TupletBracket #'stencil = ##f
    \once \override TupletNumber #'stencil = ##f
    \times 4/6  {
        r16 \> <g bes>16 ( ^. [ <bes es>16 ^. <es g>16 ^> ^. <bes es>16
        ^. <g bes>16 ) ^. ] }
    \once \override TupletBracket #'stencil = ##f
    \once \override TupletNumber #'stencil = ##f
    \times 4/6  {
        r16 <ges a>16 ( ^. [ <a es'>16 ^. <es' ges>16 ^> ^. <a, es'>16
        ^. <ges a>16 \! ) ^. ] }
    \once \override TupletBracket #'stencil = ##f
    \once \override TupletNumber #'stencil = ##f
    \times 4/6  {
        r16 <f bes>16 ( ^. [ <bes d>16 ^. <d f>16 ^> ^. <bes d>16 ^. <f
            bes>16 ) ^. ] }
    \once \override TupletBracket #'stencil = ##f
    \once \override TupletNumber #'stencil = ##f
    \times 4/6  {
        r16 <d f>16 ( _. [ <f bes>16 _. <bes d>16 _> _. <f bes>16 _. <d
            f>16 ) _. ] }
    \break \repeat volta 2 {
        | % 3
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d f>16 [ <f bes>16 <bes d>16 <f bes>16 <d f>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d e>16 [ <e bes'>16 <bes' d>16 <e, bes'>16 <d e>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d f>16 [ <f bes>16 <bes d>16 <f bes>16 <d f>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <es a>16 [ <a c>16 <c es>16 <a c>16 <es a>16 ] }
        | % 4
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d g>16 [ <g bes>16 <bes d>16 <g bes>16 <d g>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d g>16 [ <g bes>16 <bes d>16 <g bes>16 <d g>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <es g>16 [ <g c>16 <c es>16 <g c>16 <es g>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <es f>16 [ <f a>16 <a c>16 <f a>16 <es f>16 ] }
        \break | % 5
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d f>16 [ <f bes>16 <bes d>16 <f bes>16 <d f>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d f>16 [ <f bes>16 <bes d>16 <f bes>16 <d f>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 \< <d fis>16 [ <fis bes>16 <bes d>16 <fis bes>16 <d fis>16
            ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d g>16 [ <g bes>16 <bes d>16 <g bes>16 <d g>16 \! ] }
        | % 6
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 \> <e g>16 [ <g bes>16 <bes d>16 <g bes>16 <e g>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <e g>16 [ <g a>16 <a cis>16 <g a>16 <e g>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 \! <es a>16 [ <a c>16 <c es>16 <a c>16 <es a>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <es a>16 [ <a c>16 <c es>16 <a c>16 <es a>16 ] }
        \break | % 7
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d g>16 [ <g bes>16 <bes d>16 <g bes>16 <d g>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <d e>16 [ <e bes'>16 <bes' d>16 <e, bes'>16 <d e>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <c f>16 [ <f a>16 <a c>16 <f a>16 <c f>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <f g>16 [ <g b>16 <b f'>16 <g b>16 <f g>16 ] }
        | % 8
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <f a>16 [ <a c>16 <c f>16 <a c>16 <f a>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <e bes'>16 [ <bes' c>16 <c e>16 <bes c>16 <e, bes'>16 ]
            }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <f a>16 [ <a c>16 <c f>16 <a c>16 <f a>16 ] }
        \once \override TupletBracket #'stencil = ##f
        \once \override TupletNumber #'stencil = ##f
        \times 4/6  {
            r16 <f a>16 [ <a c>16 <c f>16 <a c>16 <f a>16 ] }
        }
    }

PartPTwoVoiceTwo =  \relative bes,, {
    \clef "bass" \key bes \major \time 4/4 <bes bes'>8 _\markup{ \italic
        {col Pedale} } r8 <bes bes'>8 r8 <bes bes'>8 r8 <bes bes'>8 r8 | % 2
    <bes bes'>8 r8 <bes bes'>8 r8 <bes bes'>8 r8 <bes bes'>8 r8 \break
    \repeat volta 2 {
        | % 3
        <bes bes'>8 r8 <g g'>8 r8 <f f'>8 r8 <f f'>8 r8 | % 4
        <g g'>8 r8 <g g'>8 r8 <es es'>8 r8 <f f'>8 r8 \break | % 5
        <bes bes'>8 r8 <bes bes'>8 r8 <bes bes'>8 r8 <bes bes'>8 r8 | % 6
        <a a'>8 r8 <a a'>8 r8 <fis fis'>8 r8 <fis fis'>8 r8 \break | % 7
        <g g'>8 r8 <g g'>8 r8 <a a'>8 r8 <d d'>8 r8 | % 8
        <c c'>8 r8 <c c'>8 r8 <f f'>8 r8 <f f'>8 r8 }
    }


% The score definition
\score {
    <<
        \new Staff <<
            \set Staff.instrumentName = "Voice"
            \context Staff << 
                \context Voice = "PartPOneVoiceOne" { \PartPOneVoiceOne }
                \new Lyrics \lyricsto "PartPOneVoiceOne" \PartPOneVoiceOneLyricsOne
                \new Lyrics \lyricsto "PartPOneVoiceOne" \PartPOneVoiceOneLyricsTwo
                \new Lyrics \lyricsto "PartPOneVoiceOne" \PartPOneVoiceOneLyricsThree
                >>
            >>
        \new PianoStaff <<
            \set PianoStaff.instrumentName = "Piano"
            \context Staff = "1" << 
                \context Voice = "PartPTwoVoiceOne" { \PartPTwoVoiceOne }
                >> \context Staff = "2" <<
                \context Voice = "PartPTwoVoiceTwo" { \PartPTwoVoiceTwo }
                >>
            >>
        
        >>
    \layout {}
    % To create MIDI output, uncomment the following line:
    %  \midi {}
    }

