\version "2.12.2"
#(set-global-staff-size 20)

% un-comment the next line to remove Lilypond tagline:
% \header { tagline="" }

\paper {
  print-all-headers = ##t % allow per-score headers

  % un-comment the next line for A5:
  % #(set-default-paper-size "a5" )

  % un-comment the next line for no page numbers:
  % print-page-number = ##f

  % un-comment the next 3 lines for a binding edge:
  % two-sided = ##t
  % inner-margin = 20\mm
  % outer-margin = 10\mm

  % un-comment the next line for a more space-saving header layout:
  % scoreTitleMarkup = \markup { \center-column { \fill-line { \magnify #1.5 { \bold { \fromproperty #'header:dedication } } \magnify #1.5 { \bold { \fromproperty #'header:title } } \fromproperty #'header:composer } \fill-line { \fromproperty #'header:instrument \fromproperty #'header:subtitle \smaller{\fromproperty #'header:subsubtitle } } } }
}

\score {
<< \override Score.BarNumber #'break-visibility = #end-of-line-invisible
\set Score.barNumberVisibility = #(every-nth-bar-number-visible 5)

% === BEGIN JIANPU STAFF ===
    \new RhythmicStaff \with {
    \consists "Accidental_engraver"
    % Get rid of the stave but not the barlines.
    % This changes between Lilypond versions.
    % \remove Staff_symbol_engraver % worked pre-2.18, but 2.18 results in missing barlines (adding Barline_engraver won't help). Do this instead:
    \override StaffSymbol #'line-count = #0 % tested in 2.15.40, 2.16.2, 2.18.0 and 2.18.2
    \override BarLine #'bar-extent = #'(-2 . 2) % LilyPond 2.18: please make barlines as high as the time signature even though we're on a RhythmicStaff (2.16 and 2.15 don't need this although its presence doesn't hurt; Issue 3685 seems to indicate they'll fix it post-2.18)
    }
    { \new Voice="jianpu" {
    \override Staff.TimeSignature #'style = #'numbered
    \override Staff.Stem #'transparent = ##t
    \override Beam #'transparent = ##f % (needed for LilyPond 2.18 or the above switch will also hide beams)
    \override Stem #'direction = #DOWN
    \override Stem #'length-fraction = #0.5
    \override Beam #'beam-thickness = #0.1
    \override Beam #'length-fraction = #0.5
    \override Voice.Rest #'style = #'neomensural % this size tends to line up better (we'll override the appearance)
    \override Accidental #'font-size = #-4
    \override Tie #'staff-position = #2.5
    \override TupletBracket #'bracket-visibility = ##t
    \tupletUp

\mark \markup{1=C}
\time 2/4
#(define (note-one grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "1")))))))
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-one c''8[^.
#(define (note-nought grob grob-origin context)
  (if (grob::has-interface grob 'rest-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "0")))))))
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8]
#(define (note-seven grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "7")))))))
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b'8.[
#(define (note-six grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "6")))))))
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
#(define (note-five grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "5")))))))
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
#(define (note-three grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "3")))))))
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-one c'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-five g'16
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-five g'16
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-five g'4[ ~
#(define (note-dashfive grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "–")))))))
  \applyOutput #'Voice #note-dashfive g'4]
\repeat volta 2 {
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
  \applyOutput #'Voice #note-nought r4
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
#(define (note-four grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "4")))))))
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8]
|
  \applyOutput #'Voice #note-three e''4[^.
  \applyOutput #'Voice #note-three e''4]^.
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-one c''4[^. ~
#(define (note-dashone grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "–")))))))
  \applyOutput #'Voice #note-dashone c''4]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
  \applyOutput #'Voice #note-nought r4
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
|
  \applyOutput #'Voice #note-five g'4[
  \applyOutput #'Voice #note-five g'4]
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-four f'4[ ~
#(define (note-dashfour grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "–")))))))
  \applyOutput #'Voice #note-dashfour f'4]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
#(define (note-two grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "2")))))))
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d'8]
  \applyOutput #'Voice #note-nought r4
}
\alternative { {
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
  \applyOutput #'Voice #note-nought r4
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d'8[
  \applyOutput #'Voice #note-five g'4
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four fis'8[
  \applyOutput #'Voice #note-two d'4
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-five g'4[ ~
(
  \applyOutput #'Voice #note-dashfive g'4]
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-five g'4[ ~
)
  \applyOutput #'Voice #note-dashfive g'4]
} {
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
}}
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-one c''8[^.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8]
  \applyOutput #'Voice #note-nought r4
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d'8[
  \applyOutput #'Voice #note-six a'4
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b8[-\tweak #'X-offset #0.6 _.
  \applyOutput #'Voice #note-five g'4
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b8]-\tweak #'X-offset #0.6 _.
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-one c'4[ ~
(
  \applyOutput #'Voice #note-dashone c'4]
|
  \applyOutput #'Voice #note-one c'4[
)
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
|
  \applyOutput #'Voice #note-one c''4.[^.
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16[
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-five gis'16
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8]
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-three e'4[ ~
(
#(define (note-dashthree grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "–")))))))
  \applyOutput #'Voice #note-dashthree e'4]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8[
)
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
|
  \applyOutput #'Voice #note-six a'4[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five gis'8.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
|
  \applyOutput #'Voice #note-one c''4[^.
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16]^.
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-two dis''4[^. ~
(
#(define (note-dashtwo grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "–")))))))
  \applyOutput #'Voice #note-dashtwo dis''4]
|
  \applyOutput #'Voice #note-two dis''4[^.
)
  \applyOutput #'Voice #note-six a'4]
|
  \applyOutput #'Voice #note-three e''4.[^.
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d''8]^.
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16[^.
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-seven b'16
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five gis'8.[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
|
  \applyOutput #'Voice #note-two d'4.[
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16[^.
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-seven b'16
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16^.
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-two d''16]^.
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e''8[^.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b'8.[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16]^.
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-six a'4[ ~
(
#(define (note-dashsix grob grob-origin context)
  (if (grob::has-interface grob 'note-head-interface)
    (begin
      (ly:grob-set-property! grob 'stencil
        (grob-interpret-markup grob
          (make-lower-markup 0.5 (make-bold-markup "–")))))))
  \applyOutput #'Voice #note-dashsix a'4]
|
  \applyOutput #'Voice #note-six a'4[
)
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
|
  \applyOutput #'Voice #note-one c''4.[^.
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16[
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-five gis'16
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five g'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8]
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-three e'4[ ~
(
  \applyOutput #'Voice #note-dashthree e'4]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8[
)
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e'8]
|
  \applyOutput #'Voice #note-six a'4[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five gis'8.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
|
  \applyOutput #'Voice #note-one c''4[^.
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16]^.
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-two dis''4[^. ~
(
  \applyOutput #'Voice #note-dashtwo dis''4]
|
  \applyOutput #'Voice #note-two dis''4[^.
)
  \applyOutput #'Voice #note-six a'4]
|
  \applyOutput #'Voice #note-three e''4.[^.
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-two d''8]^.
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16[^.
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-seven b'16
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-five gis'8.[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-six a'16]
|
  \applyOutput #'Voice #note-two d'4.[
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-four f'8]
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-six a'8[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16[^.
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-seven b'16
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16^.
\set stemLeftBeamCount = #2
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-two d''16]^.
| \set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-three e''8[^.
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-nought r8]
\set stemLeftBeamCount = #0
\set stemRightBeamCount = #1
  \applyOutput #'Voice #note-seven b'8.[
\set stemLeftBeamCount = #1
\set stemRightBeamCount = #2
  \applyOutput #'Voice #note-one c''16]^.
\once \override Tie #'transparent = ##t \once \override Tie #'staff-position = #0 |
  \applyOutput #'Voice #note-six a'4[ ~
(
  \applyOutput #'Voice #note-dashsix a'4]
|
  \applyOutput #'Voice #note-six a'4[
)
  \applyOutput #'Voice #note-five g'4]
\ff
\bar "|."
} }
% === END JIANPU STAFF ===

>>
\header{
title="博基上校进行曲"
}
\layout{} }
\score {
\unfoldRepeats
<< 

% === BEGIN MIDI STAFF ===
    \new Staff { \new Voice="midi" {
\transpose c c { \time 2/4 c''8 r8 b'8. a'16 | g'8 e'8 c'8 e'8 | g'8 g'16 a'16 g'8 g'16 a'16 | g'4 ~ g'4 \repeat volta 2 { | g'8 e'8 r4 | r8 e'8 f'8 g'8 | e''4 e''4 | c''4 ~ c''4 | g'8 e'8 r4 | r8 e'8 f'8 e'8 | g'4 g'4 | f'4 ~ f'4 | f'8 d'8 r4 } \alternative { { | r8 d'8 e'8 f'8 | g'8 e'8 r4 | r8 e'8 f'8 e'8 | d'8 g'4 e'8 | fis'8 d'4 a'8 | g'4 ~ ( g'4 | g'4 ~ ) g'4 } { | r8 a'8 b'8 a'8 }} | c''8 g'8 r4 | r8 g'8 f'8 e'8 | d'8 a'4 d'8 | b8 g'4 b8 | c'4 ~ ( c'4 | c'4 ) e'8 e'8 | c''4. b'8 | a'16 gis'16 a'8 g'8 f'8 | e'4 ~ ( e'4 | e'8 ) e'8 f'8 e'8 | a'4 gis'8. a'16 | c''4 a'8. c''16 | dis''4 ~ ( dis''4 | dis''4 ) a'4 | e''4. d''8 | c''16 b'16 a'8 gis'8. a'16 | d'4. f'8 | a'8 r8 c''16 b'16 c''16 d''16 | e''8 r8 b'8. c''16 | a'4 ~ ( a'4 | a'4 ) e'8 e'8 | c''4. b'8 | a'16 gis'16 a'8 g'8 f'8 | e'4 ~ ( e'4 | e'8 ) e'8 f'8 e'8 | a'4 gis'8. a'16 | c''4 a'8. c''16 | dis''4 ~ ( dis''4 | dis''4 ) a'4 | e''4. d''8 | c''16 b'16 a'8 gis'8. a'16 | d'4. f'8 | a'8 r8 c''16 b'16 c''16 d''16 | e''8 r8 b'8. c''16 | a'4 ~ ( a'4 | a'4 ) g'4 \ff }
} }
% === END MIDI STAFF ===

>>
\header{
title="博基上校进行曲"
}
\midi { \context { \Score tempoWholesPerMinute = #(ly:make-moment 84 4)}} }
