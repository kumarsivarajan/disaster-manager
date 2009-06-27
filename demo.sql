--
-- Zrzut danych tabeli `message`
--

INSERT INTO `message` (`id`, `message`, `read`, `date`) VALUES
(1, 'Procedura demonstrująca użycie czujnika sprzętowego została zakończona.', 0, '2009-06-27 03:15:27'),
(2, 'Procedura demonstrująca użycie czujnika sprzętowego została zakończona.', 0, '2009-06-27 03:17:21');

--
-- Zrzut danych tabeli `probe`
--

INSERT INTO `probe` (`id`, `name`, `interval`, `port`, `state`, `procedure`) VALUES
(1, 'Automatyczny test sprzętowego czujnika', 5, 1, 1, 1);

--
-- Zrzut danych tabeli `procedure`
--

INSERT INTO `procedure` (`id`, `name`, `description`, `active`, `added`) VALUES
(1, 'Demonstracja użycia sprzętowego czujnika', 'Procedura aktywuje port 0, czeka na sygnał z portu 0, następnie deaktywuje port 0, aktywuje port 1 i wysyła wiadomość do operatora.', 1, 1);

--
-- Zrzut danych tabeli `procedure_action`
--

INSERT INTO `procedure_action` (`id`, `procedure`, `label`, `next_action`, `type`, `arguments`, `maxtime`, `added`) VALUES
(1, 1, 'Aktywacja pierwszego portu', 2, 5, '0,1', NULL, 1),
(2, 1, 'Oczekiwanie na reakcje', 3, 6, '0,1', 8, 1),
(3, 1, 'Deaktywacja pierwszego portu', 4, 5, '0,0', NULL, 1),
(4, 1, 'Aktywacja drugiego portu', 5, 5, '1,1', NULL, 1),
(5, 1, 'Komunikat o końcu procedury', NULL, 1, 'Procedura demonstrująca użycie czujnika sprzętowego została zakończona.', NULL, 1);

--
-- Zrzut danych tabeli `report`
--

INSERT INTO `report` (`id`, `test`, `procedure`, `procedure_name`, `date`, `error`) VALUES
(1, 0, 1, 'Demonstracja użycia sprzętowego czujnika', '2009-06-27 03:15:23', NULL),
(2, 0, 1, 'Demonstracja użycia sprzętowego czujnika', '2009-06-27 03:16:13', 'ActionException: model.SerialCommunicationException: Czas przekroczony'),
(3, 0, 1, 'Demonstracja użycia sprzętowego czujnika', '2009-06-27 03:16:53', NULL);

--
-- Zrzut danych tabeli `report_action`
--

INSERT INTO `report_action` (`report`, `order`, `type`, `arguments`, `maxtime`, `usedtime`, `date`) VALUES
(1, 1, 5, '0,1', NULL, 0, '2009-06-27 03:15:23'),
(1, 2, 6, '0,1', 8, 4, '2009-06-27 03:15:27'),
(1, 3, 5, '0,0', NULL, 0, '2009-06-27 03:15:27'),
(1, 4, 5, '1,1', NULL, 0, '2009-06-27 03:15:27'),
(1, 5, 1, 'Procedura demonstrująca użycie czujnika sprzętowego została zakończona.', NULL, 0, '2009-06-27 03:15:27'),
(2, 1, 5, '0,1', NULL, 1, '2009-06-27 03:16:14'),
(2, 2, 6, '0,1', 8, 13, '2009-06-27 03:16:27'),
(3, 1, 5, '0,1', NULL, 1, '2009-06-27 03:16:54'),
(3, 2, 6, '0,1', 8, 27, '2009-06-27 03:17:21'),
(3, 3, 5, '0,0', NULL, 0, '2009-06-27 03:17:21'),
(3, 4, 5, '1,1', NULL, 0, '2009-06-27 03:17:21'),
(3, 5, 1, 'Procedura demonstrująca użycie czujnika sprzętowego została zakończona.', NULL, 0, '2009-06-27 03:17:21');
