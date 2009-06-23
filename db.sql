-- --------------------------------------------------------

--
-- Struktura tabeli dla  `message`
--

CREATE TABLE `message` (
  `id` mediumint(8) unsigned NOT NULL auto_increment,
  `message` text collate utf8_polish_ci NOT NULL,
  `read` tinyint(1) NOT NULL default '0' COMMENT 'czy wiadomość została przeczytana',
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci COMMENT='wiadomości dla operatora';

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `probe`
--

CREATE TABLE `probe` (
  `id` smallint(5) unsigned NOT NULL auto_increment,
  `name` varchar(64) collate utf8_polish_ci NOT NULL,
  `type` tinyint(3) unsigned NOT NULL COMMENT 'typ czujnika - szczegóły w kodzie',
  `query` varchar(255) collate utf8_polish_ci NOT NULL COMMENT 'zapytanie do czujnika',
  `interval` mediumint(8) unsigned NOT NULL COMMENT 'co ile sekund, czujnik jest odpytywany',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `probe_values`
--

CREATE TABLE `probe_values` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `probe` smallint(5) unsigned NOT NULL,
  `value_min` bigint(20) NOT NULL,
  `value_max` bigint(20) NOT NULL,
  `procedure` smallint(5) unsigned NOT NULL COMMENT 'procedura uruchamiana, jeżeli wartość zwrócona przez czujnik jest w zakresie [value_min, value_max]',
  PRIMARY KEY  (`id`),
  KEY `probe` (`probe`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `procedure`
--

CREATE TABLE `procedure` (
  `id` smallint(5) unsigned NOT NULL auto_increment,
  `name` varchar(128) collate utf8_polish_ci NOT NULL,
  `description` text collate utf8_polish_ci NOT NULL,
  `active` tinyint(1) NOT NULL,
  `added` tinyint(1) NOT NULL default '0' COMMENT 'czy zakończono proces dodawania procedury',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `procedure_action`
--

CREATE TABLE `procedure_action` (
  `id` mediumint(8) unsigned NOT NULL auto_increment,
  `procedure` smallint(5) unsigned NOT NULL,
  `label` varchar(64) collate utf8_polish_ci default NULL COMMENT 'opcjonalna nazwa akcji',
  `next_action` mediumint(8) unsigned default NULL COMMENT 'ID następnej akcji w procedurze',
  `type` tinyint(3) unsigned NOT NULL COMMENT 'rodzaj akcji (szczegóły w kodzie)',
  `arguments` text collate utf8_polish_ci NOT NULL COMMENT 'parametry - każda akcja ma inne',
  `maxtime` mediumint(8) unsigned default NULL COMMENT 'maksymalny czas, jaki _powinna_ zająć akcja',
  `added` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `procedure` (`procedure`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `report`
--

CREATE TABLE `report` (
  `id` smallint(5) unsigned NOT NULL auto_increment,
  `test` tinyint(1) NOT NULL COMMENT 'czy był to test, czy rzeczywisty problem',
  `procedure` smallint(5) unsigned NOT NULL COMMENT 'może odnosić się do usuniętej procedury',
  `procedure_name` varchar(128) collate utf8_polish_ci NOT NULL COMMENT 'na wypadek, gdyby procedurę usunięto',
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT 'data rozpoczęcia testu',
  `error` varchar(255) collate utf8_polish_ci default NULL COMMENT 'ewentualny komunikat błędu',
  PRIMARY KEY  (`id`),
  KEY `procedure` (`procedure`),
  KEY `date` (`date`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci COMMENT='raporty z wykonanych procedur';

-- --------------------------------------------------------

--
-- Struktura tabeli dla  `report_action`
--

CREATE TABLE `report_action` (
  `report` smallint(5) unsigned NOT NULL,
  `order` smallint(5) unsigned NOT NULL COMMENT 'która w kolei została wykonana ta akcja',
  `type` tinyint(3) unsigned NOT NULL COMMENT 'patrz procedure_action',
  `arguments` text collate utf8_polish_ci NOT NULL COMMENT 'patrz procedure_action',
  `maxtime` mediumint(8) unsigned default NULL,
  `usedtime` mediumint(8) unsigned NOT NULL COMMENT 'czas działania akcji',
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`report`,`order`),
  KEY `report` (`report`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;
