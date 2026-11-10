CREATE UNIQUE INDEX uq_hackathon_team_name_lower 
on teams (hackathon_id, (LOWER(TRIM(name)))) WHERE status <> 'CANCELLED';