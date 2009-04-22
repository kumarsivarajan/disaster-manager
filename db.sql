--
-- Baza danych: 'disaster_manager'
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'probe'
--

CREATE TABLE probe (
  id smallint(5) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate utf8_polish_ci NOT NULL,
  `type` tinyint(3) unsigned NOT NULL COMMENT 'typ czujnika - szczegóły w kodzie',
  `query` varchar(255) collate utf8_polish_ci NOT NULL COMMENT 'zapytanie do czujnika',
  `interval` mediumint(8) unsigned NOT NULL COMMENT 'co ile sekund, czujnik jest odpytywany',
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'probe_values'
--

CREATE TABLE probe_values (
  id int(10) unsigned NOT NULL auto_increment,
  probe smallint(5) unsigned NOT NULL,
  value_min bigint(20) NOT NULL,
  value_max bigint(20) NOT NULL,
  `procedure` smallint(5) unsigned NOT NULL COMMENT 'procedura uruchamiana, jeżeli wartość zwrócona przez czujnik jest w zakresie [value_min, value_max]',
  PRIMARY KEY  (id),
  KEY probe (probe)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'procedure'
--

CREATE TABLE `procedure` (
  id smallint(5) unsigned NOT NULL auto_increment,
  `name` varchar(128) collate utf8_polish_ci NOT NULL,
  description text collate utf8_polish_ci NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'procedure_action'
--

CREATE TABLE procedure_action (
  id mediumint(8) unsigned NOT NULL auto_increment,
  `procedure` smallint(5) unsigned NOT NULL,
  next_action mediumint(8) unsigned NOT NULL COMMENT 'ID następnej akcji w procedurze',
  `type` tinyint(3) unsigned NOT NULL COMMENT 'rodzaj akcji (szczegóły w kodzie)',
  arguments text collate utf8_polish_ci NOT NULL COMMENT 'parametry - każda akcja ma inne',
  maxtime mediumint(8) unsigned NOT NULL COMMENT 'maksymalny czas, jaki _powinna_ zająć akcja',
  PRIMARY KEY  (id),
  KEY `procedure` (`procedure`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'report'
--

CREATE TABLE report (
  id smallint(5) unsigned NOT NULL auto_increment,
  test tinyint(1) NOT NULL COMMENT 'czy był to test, czy rzeczywisty problem',
  `procedure` smallint(5) unsigned NOT NULL COMMENT 'może odnosić się do usuniętej procedury',
  procedure_name varchar(128) collate utf8_polish_ci NOT NULL COMMENT 'na wypadek, gdyby procedurę usunięto',
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'data rozpoczęcia testu',
  PRIMARY KEY  (id),
  KEY `procedure` (`procedure`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci COMMENT='raporty z wykonanych procedur';

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'report_action'
--

CREATE TABLE report_action (
  id mediumint(8) unsigned NOT NULL auto_increment,
  report smallint(5) unsigned NOT NULL,
  `order` smallint(5) unsigned NOT NULL COMMENT 'która w kolei została wykonana ta akcja',
  `type` tinyint(3) unsigned NOT NULL COMMENT 'patrz procedure_action',
  arguments text collate utf8_polish_ci NOT NULL COMMENT 'patrz procedure_action',
  `date` date NOT NULL,
  PRIMARY KEY  (id),
  KEY report (report)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  'user'
--

CREATE TABLE `user` (
  id smallint(5) unsigned NOT NULL auto_increment,
  login varchar(32) collate utf8_polish_ci NOT NULL,
  `hash` char(32) collate utf8_polish_ci NOT NULL COMMENT 'null tylko w momencie między dodaniem użytkownika a ustawieniem mu hasła',
  `level` tinyint(3) unsigned NOT NULL COMMENT '0: nieaktywny, 1: gość, 2: użytkownik, 3: administrator',
  PRIMARY KEY  (id),
  UNIQUE KEY login (login)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci COMMENT='użytkownicy - operatorzy systemu';

