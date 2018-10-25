DO $$
DECLARE
    tables CURSOR FOR
        select distinct table_name, table_schema from information_schema.columns 
 where column_name = 'boo_en_migration' 
	and table_schema='dso' and table_name not like 'v_%';
    
BEGIN
    FOR table_record IN tables LOOP
        EXECUTE 'update ' || table_record.table_schema || '.' || table_record.table_name || ' set boo_en_migration=false';
    END LOOP;
end;
$$;