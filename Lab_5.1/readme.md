# Zadanie 5 — `Socket`y
##### Część 1 — aplikacje konsolowe w architekturze klient-serwer, realizowane z wykorzystaniem `socket`ów

## CASE
### Aplikacja klient-serwer

Wykonaj aplikację klient-serwer realizującą funkcję `ECHO`.

#### Działanie serwera:

1. Oczekuj na przyłączenie klienta.
2. Oczekuj na odebranie tekstu od klienta.
3. Odeślij tekst klientowi.
4. Jeżeli tekst == `bye` to rozłącz klienta i idź do kroku 1.
5. Idź do kroku 2.

#### Działanie klienta:

1. Przyłącz się do serwera.
2. Oczekuj na wpisanie tekstu z klawiatury.
   1. Wyślij wpisany tekst serwerowi.
   2. Oczekuj na odebranie komunikatu od serwera.
   3. Jeśli komunikat == `bye` — KONIEC.
3. Idź do kroku 2.

### Multi-klient
Program będzie działał jak powyżej, jednak do serwera będzie możliwe niezależne podłączenie kilku
klientów. Serwer działa następująco:

1. Słuchaj na porcie 7.
2. Po przyłączeniu klienta uruchom wątek i przenieś rozmowę do wątku.
   1. Czekaj na wiadomość od klienta.
   2. Odeślij klientowi wiadomość.
   3. Jeśli wiadomość bye to zakończ wątek, jeśli nie to a).
3. Wróć do kroku 1.

## Zadania laboratoryjne

### Zadanie 1 (1 pkt)

A jeżeli byśmy chcieli, aby wysłany przez klienta komunikat dotarł do pozostałych klientów?

Sprawa nie jest taka prosta, ponieważ każdy z klientów może wysłać komunikat w dowolnym momencie.
Pozostali muszą nasłuchiwać komunikatów.

Rozwiązaniem jest zastosowanie dwóch `socket`ów:
* od klienta do serwera — dla wysyłania,
* od serwera do klienta — dla odbierania.

Ponieważ oba kanały komunikacji mają działać niezależnie, każde z połączeń powinno działać
na osobnym wątku.

### Zadanie 2 (2 pkt)

W maksymalnej wersji program będzie działał tak:
* Każdy klient ma swoje imię i w momencie połączenia rejestruje je w serwerze
* Klient może wysyłać komunikat do wszystkich lub tylko jednego klienta pisząc:
  > `ALL` Witaj świecie
  > \
  > Wacek Idziesz dzisiaj na basen?

  Adresat/adresaci powinni otrzymać na konsoli komunikat, który rozpoczniemy od nazwy nadawcy
