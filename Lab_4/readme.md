# Zadanie 4 — wątki

## CASE — Pliki i wątki

### Wstęp

Programy działające na wątkach często pracują w trybie producent-konsument. Jest to najczęściej
układ, który sam reguluje swoją szybkość działania dzięki zastosowaniu kolejki
synchronizowanej *FIFO*.

W dzisiejszym studium przypadku będziemy rozpatrywać układ, w którym jest kilku producentów, jeden
konsument oraz być może będzie istniał wątek sterujący zarządzający dostępem poszczególnych wątków.

#### Opis przypadku

W katalogu projektu powinny istnieć pliki tekstowe. Może być ich dowolna liczba. Program powinien
je odnaleźć.

Dla ułatwienia przygotuj 3 pliki wejściowe z różną zawartością tak, aby można było zidentyfikować
które znaki pochodzą z którego pliku:

* `plik1.txt` składa się wyłącznie z cyfr i białych znaków (liczby wielocyfrowe różnej długości
  oddzielone spacją, w kilku linijkach)
* `plik2.txt` składa się wyłącznie z liter małych i białych znaków (słowa różnej długości oddzielone
  spacją w kilku linijkach).
* `plik3.txt` składa się wyłącznie z liter wielkich i białych znaków (słowa różnej długości
  oddzielone spacją w kilku linijkach)

Pliki przygotuj tak, aby każdy miał inną liczbę linijek, inną liczbę słów w linijkach itp.

### Zadanie wspólne w czasie wykładu

Z każdym plikiem wejściowym wiążemy po jednym wątku typu producent, `WatekCzytajacy`.

Program otwiera również plik wyjściowy `wynik.txt` i przypisuje mu wątek typu konsument,
`WatekPiszacy`.

Każdy `WatekCzytajacy` odczytuje swój plik **znak po znaku** i umieszcza w **jednoelementowym
buforze**, który jest **zmienną współdzieloną typu `char`** dla wszystkich wątków.

`WatekPiszacy` zabiera znak z buforu i zapisuje do pliku `wynik.txt`.

Plik `wynik.txt` będzie zawierał wymieszane znaki ze wszystkich plików tekstowych.

**UWAGA**:
* Po napotkaniu końca pliku `WatekCzytajacy` kończy się.
* Każdy plik ma swoją długość, wątki nie będą się kończyć jednocześnie a zakończenie jednego z nich
  nie może blokować pozostałych.

W jaki sposób powiadomić `WatekPiszacy`, że wszystkie czytające zakończyły się? Wątek ten powinien
zamknąć się po zamknięciu plików i program powinien się zakończyć.

## Zadania laboratoryjne

### Zadanie 1 - Wątki w *JavaFX* (2 pkt)

Działamy na powyższym przykładzie.

1. Dla każdego z wątków uruchom oddzielne okno graficzne *JavaFX*, w którym jest obiekt `TextArea`
   wypełniający cały jego obszar. Tytułem okna będzie nazwa pliku.
2. `WatekCzytajacy` dopisuje odczytany znak na końcu pola tekstowego.
3. `WatekPiszacy` dopisuje znak, który zapisał do pliku na końcu pola tekstowego. Wykonaj symulację
   opóźnienia w każdym wątku (`sleep`) tak, aby było widać, jak literki dopisują się i żeby
   symulacja trwała kilkanaście sekund, zanim wszystko zniknie.

### Zadanie 2 (2 pkt)

Rozwiń powyższe zadanie w ten sposób, że w pliku wyjściowym nie będzie mieszanki liter, tylko całe
słowa z tego samego pliku, czyli wątek nie powinien oddawać sterowania, dopóki nie odczyta
całego słowa. Pamiętamy, że nadal bufor jest jednoelementowy!

Każdy biały znak, w tym `\n` znajdzie się w pliku wynikowym.

### Zadanie 3 (1 pkt)

Zachowując sposób przetwarzania (znak po znaku), zmodyfikuj program tak, aby przepisywane były całe
linie poszczególnych plików (linie przeplatają się wg kolejności podanych parametrów, np.:)

```
pierwsza linia pliku1
pierwsza linia pliku3
pierwsza linia pliku2
druga linia pliku2
druga linia pliku1
druga linia pliku3
trzecia linia pliku 2
trzecia linia pliku 3
czwarta linia pliku 3
```
