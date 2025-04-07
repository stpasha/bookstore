
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'store') THEN
        EXECUTE 'CREATE DATABASE store WITH OWNER baseadm';
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_namespace WHERE nspname = 'storedata') THEN
        EXECUTE 'CREATE SCHEMA storedata AUTHORIZATION baseadm';
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_schema = 'storedata' AND sequence_name = 'accounts_account_id_seq') THEN
        EXECUTE 'CREATE SEQUENCE storedata.accounts_account_id_seq START WITH 1 INCREMENT BY 1';
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'storedata' AND table_name = 'accounts') THEN
        EXECUTE '
            CREATE TABLE storedata.accounts (
                account_id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval(''storedata.accounts_account_id_seq''),
                amount DECIMAL(10,2) NOT NULL
            );
        ';
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'storedata' AND table_name = 'accounts' AND column_name = 'version') THEN
        EXECUTE 'ALTER TABLE storedata.accounts ADD COLUMN version BIGINT';
    END IF;
END$$;
