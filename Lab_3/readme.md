# Zadanie 3 — dynamiczna Java

### CASE 1

Korzystając z mechanizmów refleksji (klasy `Class`, `Method`), stwórz interaktywny kalkulator.
Użytkownik proszony jest o podanie z klawiatury wyrażenia w postaci `String`'a `"9 plus 6"`
lub `"2 minus 5"`, `"pierwiastek 3"`. Program powinien zwracać wynik lub generować wyjątek.
Obsłuż: _plus_, _minus_, _razy_, _dziel_, _pierwiastek_ (czyli operacje jedno i dwuargumentowe).

#### Realizacja

Działania zrealizuj jako funkcje o takich nazwach jak powyżej. Parsowanie tekstu możesz zrealizować
przez Scanner lub wyrażenia regularne. Fragment tekstu rozpoznany jako nazwa działania wywołaj
refleksyjnie. Błędną nazwę funkcji lub niewłaściwą liczbę parametrów wywołania metody wyłap
poprzez odpowiedni wyjątek. Staraj się unikać warunków, rozgałęzień.

Przygotuj testy _JUnit_ dla przygotowanej funkcji (dane prawidłowe, dane nieprawidłowe, wyjątki).

#### Wyjaśnienie — Refleksja

Refleksja to mechanizm, który pozwala na zaglądanie do konstrukcji obiektów, co więcej
na manipulację tą konstrukcją w czasie wykonania programu (`Runtime`). Przykładowo pozwala na:

* wylistowanie wszystkich metod, konstruktorów, pól, adnotacji danej klasy i jej przodków,
* utworzenie instancji obiektu przez podanie jego nazwy poprzez String, to samo z wywołaniem
  metody klasy,
* zmianę modyfikatora np. private na public dla pewnego pola klasy co pozwala na odbezpieczenie
  do niego dostępu.

### CASE 2 — Konstruktor domyślny
Zakładamy, że jest pewna klasa Student, w której jest kilka pól: `imie`, `nazwisko`,
`nr_albumu`, oraz `oceny`.

W klasie możemy zadeklarować konstruktor domyślny, w którym do pól przypisywane są domyślne
wartości, oraz inicjujący — wartości pól są parametrami.

Zaprezentuj możliwość wstawiania wartości domyślnych poprzez adnotacje.

#### Realizacja

Utwórz typ adnotacyjny dla konstrukcji domyślnego studenta.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface DefaultStudent {}
```

Zaprezentuj działanie tego interfejsu poprzez jego implementację. Inaczej mówiąc, chodzi o to,
że mamy utworzyć domyślny konstruktor dla klasy student, który będzie inicjalizował imię, nazwisko,
oceny jakimiś domyślnymi wartościami. Jednak konstruktor ten będzie czerpał te domyślne wartości
z adnotacji, tak więc nie musimy zmieniać zawartości konstruktora, aby zmienić jego działanie.

Przykładowe użycie:

```java
@DefaultStudent //lub @DefaultStudent(imie="Jan", nazwisko="Nowak") //domyślne wartości przykrywamy własnymi
public class Student {
    public Student() {
        // zawartość konstruktora domyślnego
    }
    //zawartość klasy Student
}
```

#### Wyjaśnienie — Adnotacje
* adnotacje możemy podzielić na wbudowane oraz własne; adnotacja własna jest interfejsem,
* adnotacje mogą być bezparametrowe lub mieć parametr (o nazwie value) lub więcej parametrów
  (o różnych nazwach i typach) i mogą mieć one wartości domyślne,
* adnotacje mogą być przypisane do klas, metod, konstruktorów oraz pól klasy; nie można przypisywać
  adnotacji do zamiennych lokalnych; adnotacje odczytujemy poprzez mechanizm refleksji,
* adnotacje mogą być przetwarzane na etapie kompilacji (np. `@Override`, `@Deprecated`,
  `@SuppressWoranings`), budowania klas (adnotacje używane do mechanizmu ORM w bazach danych)
  lub czasu wykonania.

## Zadania laboratoryjne
### Zadanie 1 (2 pkt)

Dla każdego obiektu można zdefiniować funkcję `toString`, która jest wywoływana niejawnie podczas
konwersji obiektu na `String`, przykładowo
```java
// jeśli w `Student` jest metoda `toString`, to zostanie tu użyta
String s1 = "obiekt: " + new Student("Jan", "Nowak");
// wyświetli obiekt, korzystając z jego metody `toString`.
System.out.println(new Student("Jan", "Nowak"));
```
Korzystając z mechanizmów refleksji (klasy `Class`, `Method`), napisz program, który będzie
automatycznie tworzył funkcję `toString` dla dowolnej klasy i wszystkich, które po niej dziedziczą.

#### Realizacja:

Załóżmy, że mamy klasę `Student`, w niej kilka pól prywatnych a do części z nich istnieją metody
dostępowe (`get...`)

Funkcja `toString` powinna znaleźć wszystkie metody dostępowe i skonstruować `String`,
zawierający wynik ich działania. Poszczególne pola rozdzielamy znakiem `\t`.
Użyj mechanizmu dziedziczenia, czyli `toString` jest w klasie bazowej. Zaprezentuj na dwóch
końcowych klasach.

### Zadanie 2 — równość obiektów (2 pkt)
Proszę utworzyć przedefiniowaną metodę equals działającą w standardowy sposób — porównującą
wszystkie pola obiektu student. Sprawdź,
[jakie wymagania powinna spełniać ta metoda](https://javastart.pl/baza-wiedzy/programowanie-obiektowe/metoda-equals)

**UWAGA:** Listę pól odczytujemy refleksyjnie, nie wpisujemy ręcznie!!!

Utwórz typ adnotacyjny:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface IgnoreEquals {}
```
Chodzi o przerobienie metody equals tak, aby wybiórczo ignorowała niektóre pola klasy `Student`
przy porównywaniu obiektów np.:

Dla takich adnotacji
```java
class Student{
@IgnoreEquals
int indeks;
@IgnoreEquals
List<Float>oceny;
String imie;
String nazwisko;
@IgnoreEquals
String stopien_studiow;
// ...
}
```
Metoda `equals` będzie brać pod uwagę wyłącznie imię i nazwisko (zdarza się, że ta sama osoba idzie
na drugi stopień, dostaje nowy numer indeksu).
Jeżeli teraz zmienimy adnotacje przy polach klasy — nie musimy modyfikować metody `equals`,
aby zmienić jej działanie.

Funkcja `equals` powinna być uniwersalna na tyle, że jeśli zmienimy listę pól obiektu
`Student` — też powinna działać.

### Zadanie 3 Funkcja `toString` z Adnotacjami (2 pkt)
Wykonaj podobnie jak w zadaniu 1 obiekt z funkcją `toString`, z tym że powinna ona działać
na bazie adnotacji.

Utwórz własny typ adnotacyjny typu Runtime, który użyjesz w dowolnej klasie (np. `Student`)
w następujący sposób.

```java
public class Student{
@DisplayAnno(comment="numer indeksu", order="2")
int indeks;
@DisplayAnno(comment="Lista ocen", order="3")
List<Float>oceny;
String imie;
@DisplayAnno(comment="Nazwisko", order="1")
String nazwisko;
boolean dzienny;
// ...
}
```
W klasie tej będzie nadpisana metoda `toString`, która powinna dla powyższego przypadku wypisać
informacje o studencie w następujący sposób:

```
Nazwisko: Kowalski, numer indeksu: 183123, Lista ocen: [3, 3.5, 5].
```
Jeśli nie ma adnotacji przy danym polu, nie będzie ono uwzględniane przy wyświetlaniu studenta.
Ważna jest również kolejność wyświetlania informacji o studencie.

Wykonaj program tak, aby:

* lista atrybutów była zmienna (funkcja `toString` pozostaje ta sama, a manipulujemy polami klasy
  i adnotacjami),
* działało dziedziczenie (bez modyfikacji `toString` tworzymy klasę pochodną, w której
  jest dodatkowe pole i przy nim adnotacja),
* numerki definiujące kolejność nie koniecznie muszą zaczynać się od 1 (mogą być, np. 10,20,30).
