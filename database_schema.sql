CREATE DATABASE IF NOT EXISTS football_fixture;
USE football_fixture;

CREATE TABLE IF NOT EXISTS teams (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    country TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS players (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    age INTEGER NOT NULL,
    position TEXT NOT NULL,
    team_id INTEGER,
    squad_no INTEGER NOT NULL,
    goal_score INTEGER NOT NULL DEFAULT 0,
    yellow_card INTEGER NOT NULL DEFAULT 0,
    red_card INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (team_id) REFERENCES teams(id)
);

CREATE TABLE stadiums (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    city TEXT NOT NULL,
    capacity INTEGER NOT NULL
);

CREATE TABLE matches (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    home_team_id INTEGER,
    away_team_id INTEGER,
    date_time DATETIME NOT NULL,
    stadium_id INTEGER,
    FOREIGN KEY (home_team_id) REFERENCES teams(id),
    FOREIGN KEY (away_team_id) REFERENCES teams(id),
    FOREIGN KEY (stadium_id) REFERENCES stadiums(id)
);

CREATE TABLE coaches (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    team_id INTEGER,
    experience_years INTEGER NOT NULL,
    nationality TEXT NOT NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id)
);

CREATE TABLE match_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_id INTEGER,
    home_team_score INTEGER NOT NULL,
    away_team_score INTEGER NOT NULL,
    FOREIGN KEY (match_id) REFERENCES matches(id)
);

CREATE TABLE fixtures (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_id INTEGER NOT NULL,
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('Scheduled', 'Completed', 'Postponed')),
    FOREIGN KEY (match_id) REFERENCES matches(id)
);


CREATE TABLE IF NOT EXISTS leagues (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    country TEXT NOT NULL,
    season TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);


CREATE TABLE IF NOT EXISTS league_teams (
    league_id INTEGER,
    team_id INTEGER,
    PRIMARY KEY (league_id, team_id),
    FOREIGN KEY (league_id) REFERENCES leagues(id),
    FOREIGN KEY (team_id) REFERENCES teams(id)
);


CREATE TABLE IF NOT EXISTS league_matches (
    league_id INTEGER,
    match_id INTEGER,
    PRIMARY KEY (league_id, match_id),
    FOREIGN KEY (league_id) REFERENCES leagues(id),
    FOREIGN KEY (match_id) REFERENCES matches(id)
);
