#Contract Registry App

## Creating Metastore tables

### With parquet tables
```sql
#change parquet anme and paths according to your case
create external table stg_attachments like parquet "/user/Jakub.Augustin/data/tbl-attachments/part-00000-715bdb8d-982b-4c78-8347-7ac0604a2102-c000.snappy.parquet"
stored as parquet
location "/user/Jakub.Augustin/data/tbl-attachments/";

compute stats stg_attachments;
show table stats stg_attachments;
show column stats stg_attachments;

#change parquet anme and paths according to your case
create external table stg_registry like parquet "/user/Jakub.Augustin/data/tbl-registry/part-00000-5c76ced4-6cdc-4e85-85cd-2f4e996d7fb0-c000.snappy.parquet"
stored as parquet
location "/user/Jakub.Augustin/data/tbl-registry/"

compute stats stg_registry;
show table stats stg_registry;
show column stats stg_registry;

#change parquet anme and paths according to your case
create external table stg_contract_party like parquet "/user/Jakub.Augustin/data/tbl-contract-party/part-00000-b16fc6ef-5362-4e78-aace-5eb20fe7deb3-c000.snappy.parquet"
stored as parquet
location "/user/Jakub.Augustin/data/tbl-contract-party/"

compute stats stg_contract_party;
show table stats stg_contract_party;
show column stats stg_contract_party;

```

### With CSV tables
