CREATE TABLE melding
(
    id                uuid primary key,
    opprettet_av      varchar(11) not null,
    bedrifts_nr       varchar(9)  not null,
    varslet_nav_dato  date        not null,
    start_dato        date        not null,
    slutt_dato        date,
    ukjent_slutt_dato boolean
);
