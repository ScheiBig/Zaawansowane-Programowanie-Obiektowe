# Zadanie 1 - Testy jednostkowe

## CASE:
Zaprojektuj aplikację konsolową do sprawdzania numeru PESEL, podanego z linii poleceń.
Jeśli nie podamy parametru, program pyta o numer PESEL

## Testy jednostkowe
Przygotuj strukturę obiektową tak, aby program mógł być w łatwy sposób testowany
z wykorzystaniem testów jednostkowych JUnit. Zalecenia:

* Testujemy tylko funkcje publiczne (czyli API). Nie testujemy funkcji prywatnych klasy.
* Jedna funkcja (metoda klasy) powinna wykonywać jedną konkretną czynność (algorytm).
* Funkcja powinna być możliwa do wywołania w środowisku izolowanym, mimo że jest składową klasy.
* Funkcja powinna: pobierać parametr (najlepiej jeden), zwracać wartość, może rzucać wyjątek.

Wykonaj klasy dla testów jednostkowych przy użyciu JUnit. Testy mają sprawdzić następujące warunki:

* wprowadzamy ciąg znaków o niewłaściwym formacie — funkcja ma rzucić wyjątek,
* wprowadzamy właściwy PESEL — funkcja sprawdzająca poprawność ma zwrócić prawdę. Funkcja 
  nie może rzucić wyjątku,
* wprowadzamy błędny PESEL (ale we właściwym formacie) - funkcja sprawdzająca poprawność 
  ma zwrócić fałsz. Funkcja nie może rzucić wyjątku,
* wprowadzamy numer dla kobiety/mężczyzny — funkcja ma zwrócić K lub M,
* wprowadzamy poprawny numer — funkcja sprawdza, czy zgadza się data urodzenia oraz płcią.

### Wsadowe przetwarzanie testów jednostkowych

Zakładamy, że mamy plik CSV, zawierający numery PESEL i odpowiadające im wyniki (Kobieta,
mężczyzna, data urodzenia, numer niepoprawny).

Przygotuj testy jednostkowe umożliwiające zaczytanie tego pliku i wykonanie wszystkich,
zawartym w nim testów.

### Obsługa wyjątków

Wyjątek, który może generować metoda, to wyjście awaryjne, które nastąpi przy niewłaściwym
formacie danych wejściowych przykładowo:

* ciąg znaków, czyli numer PESEL zawiera nie tylko cyfry, ale inne znaki,
* ciąg znaków jest niezainicjowany (`null`),
* ciąg znaków ma niewłaściwą długość.

Program wykonaj tak, aby zminimalizować użycie warunków `if`, `while` na rzecz wyjątków.
