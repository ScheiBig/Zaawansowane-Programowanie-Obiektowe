# Zadanie 2 - przetwarzanie strumieniowe

## Dane źródłowe

Wejdź na chwilę [tutaj](http://pizzachili.dcc.uchile.cl/texts/nlang/).

Tam są skompresowane książki w języku angielskim. Po prostu czysty tekst. Dla ułatwienia, plik,
który będziecie przetwarzać został pobrany i umieszczony na wikampie (poniżej w załączniku).

Proszę rozpakować ręcznie plik i przejrzeć, w jakiej postaci są te książki.
Plik powinien mieć 200MB.

## CASE WYKŁADOWY 1 - WCZYTYWANIE PLIKU

Będziemy potrzebować aby odczytać podany plik tekstowy słowo po słowie tak, aby kolejno
je przetworzyć. Sprawdzimy szybkość działania wczytywania słów z pliku w różnych wariantach,
oraz sprawdzimy zajętość pamięci. Szukamy optymalnego rozwiązania.

Dodatkowe wymaganie - tekst powinien być oczyszczony następująco:

* wszystkie litery zamieniamy na małe,
* usuwamy wszystko co nie jest literą (czyli cyfry, znaki specjalne, przestankowe,
  apostrofy cudzysłowy, itp),
* będą nas interesować wyłącznie słowa o długości co najmniej 3 liter.
 
## CASE WYKŁADOWY 2 - jak znaleźć najczęstsze słowa w pliku?

Znajdźmy słowa występujące co najmniej N/k razy, gdzie k jest parametrem,
a N liczbą wszystkich słów.

### Podejście 1

Zbudujemy histogram częstości występowania słów w tekście. Spróbujemy to zoptymalizować
pod względem szybkości działania oraz zajętości pamięci.

### Podejście 2

Własne pomysły studentów.

### Podejście 3
Algorytm Misry - Griesa.

## ZADANIE NA OCENĘ

### Polecenia i propozycje testów jednostkowych

* Sprawdź, czy w pliku english.200MB.txt jest 39'177'225 słów.
* Wykonaj strumieniowo filtr, który kolejno:
  * sprowadzi wszystkie słowa do małych liter,
  * zamieni każdy znak nie będący literą na spację,
  * usunie słowa krótsze niż 3 znaki.
* Sprawdź, czy w pliku english.200MB.txt jest 29'250'532 przefiltrowanych słów.

### Algorytm Misry–Griesa (strumieniowy, 2-przebiegowy)

* parametr wejściowy: *k*,
* zakładamy słownik par: *klucz* → *liczba wystąpień*,
* tworzymy puste na starcie słowniki *D1*, *D2*,

1. Przebieg pierwszy:
   * Pobierz kolejne słowo ze strumienia sprawdzając czy występuje w słowniku *D1*.
   * Jeśli liczba elementów w słowniku jest < *k* – 1:
     * jeśli słowo nie jest *kluczem* słownika, to stwórz nowy element o takim *kluczu*
       i nadaj mu *wartość* 1,
     * jeśli słowo jest *kluczem* słownika, to zwiększ *wartość* tego elementu o 1.
   * Jeśli liczba elementów w słowniku jest *k* - 1:
     * jeśli słowo jest *kluczem* słownika, to zwiększ *wartość* tego elementu o 1,
     * jeśli słowo jest nowe (tj. nie jest kluczem w *D1*), to nie dodajemy go do słownika.
       Wartość każdego elementu w słowniku zmniejszamy o 1. Jeśli któraś z wartości 
       stanie się zerem, to usuwamy cały element (tj. parę *klucz-wartość*).
2. Przebieg drugi:
   * Pobierz słowo ze strumienia,
   * Jeśli słowo występuje w *D1*, to utwórz w *D2* element *słowo->liczba* wystąpień 
     (lub zaktualizuj)
3. Na końcu: 
   * Przefiltruj wynikowy słownik *D2*, pozostawiając w tym tylko te pary *klucz-wartość*,
     dla których *wartość* > *n*/*k*. \
     **Uwaga:** *n* nie jest podane na wejściu, więc wcześniej ("po drodze") musisz 
     znaleźć jego wartość.

### Algorytm główny
Wykonaj algorytm *M-G* i uruchom go na `english.200.MB`, filtrowanym w trakcie 
wczytywania, dla `k = 100`.

#### Warunki:

* cały algorytm wraz z odczytem pliku i jego filtracją ma się wykonać w czasie poniżej 25s.
  Weź pod uwagę ["wygrzewanie JVM"](https://www.baeldung.com/java-jvm-warmup).
* uruchom program z linii poleceń z przełącznikiem  `-Xmx20m`. Oznacza to, że całkowita
  pamięć zajmowana przez program, wraz z jego zmiennymi, nie może przekroczyć 20MB.
  Program musi się wykonać. Będzie to oznaczać, że działa strumieniowo a jedynymi strukturami
  obciążającymi pamięć są słowniki D1 i D2

#### Program powinien wypisać:
```yaml
the: 2427360
and: 1323210
that: 457507
was: 405046
his: 364337
with: 313077
```
