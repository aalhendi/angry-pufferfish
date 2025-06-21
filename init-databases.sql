CREATE DATABASE account_db;
CREATE DATABASE customer_db;

-- NOTE(aalhendi): this is not a good practice, its a demo
CREATE USER account_user WITH PASSWORD 'account_pass';
CREATE USER customer_user WITH PASSWORD 'customer_pass';

-- NOTE(aalhendi): neither is this...
GRANT ALL PRIVILEGES ON DATABASE account_db TO account_user;
GRANT ALL PRIVILEGES ON DATABASE customer_db TO customer_user;

\c account_db;
GRANT ALL ON SCHEMA public TO account_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO account_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO account_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO account_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO account_user;

\c customer_db;
GRANT ALL ON SCHEMA public TO customer_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO customer_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO customer_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO customer_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO customer_user; 