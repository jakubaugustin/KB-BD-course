# Cleaning data with Spark SQL

## Data quality checks in Spark SQL
CDSW, Zeppelin or Jupiter for testing

```scala
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

//Load data from parquet
val registryData : Dataset[Row] = spark.read.parquet("data/tbl-registry/*")
val contractPartyData : Dataset[Row] = spark.read.parquet("data/tbl-contract-party/*")
val attachmentsData : Dataset[Row] = spark.read.parquet("data/tbl-attachments/*")

//Register table on top of DF
registryData.createOrReplaceTempView("registry")
contractPartyData.createOrReplaceTempView("contractParty")
attachmentsData.createOrReplaceTempView("attachments")

//Describe table to see it`s contents
spark.sql("""describe registry""").show(1000, false)

//casZverejneni - check timestamp format
//Original format is OK, but not standard so let`s cast it
spark.sql("""
select
  casZverejneni casZverejneniOld
  ,cast(casZverejneni as timestamp) casZverejneni
from registry
""").show(false)

//Check if data was casted OK - expect length of 19
spark.sql("""
select
  case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end date_cast_check
from registry
where length(cast(casZverejneni as timestamp)) <> 19
""").show(false)

//Check idSmlouvy
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end idSmlouvy_check
from registry
where idSmlouvy IS NULL
""").show(false)

// idVerze
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end idVerze_check
from registry
where idVerze IS NULL
""").show(false)

//id
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end id_check
from registry
where id IS NULL
""").show(false)

//Odkaz field -check nulls
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end odkaz_check
from registry
where odkaz is null
""").show(false)

//platnyZaznam - should be just boolean, cast
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end platnyZaznam_check 
from registry
""").show(false)

spark.sql("""
select
   cast(platnyZaznam as boolean), platnyZaznam
from registry
""").show(false)

//cisloSmlouvy - check nulls
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end cisloSmlouvy_check
from registry
where cisloSmlouvy is null
""").show(false)

//hodnota
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end hodnota_check
from registry
where hodnota is null
""").show(false)

//mena
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end mena_check
from registry
where mena is null
""").show(false)

spark.sql("""
select
   mena
   , count(*)
from registry
group by 1
order by 2 desc
""").show(false)

//datumUzavreni - check null and cast
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end datumUzavreni_check
from registry
where datumUzavreni is null
""").show(false)

//Original format is OK, but not standard so let`s cast it
spark.sql("""
select
  datumUzavreni datumUzavreniOld
  ,cast(datumUzavreni as timestamp) datumUzavreni
from registry
""").show(false)

//hodnotaBezDph
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end hodnotaBezDph_check
from registry
where hodnotaBezDph is null
""").show(false)

//hodnotaVcetneDph
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end hodnotaVcetneDph_check
from registry
where hodnotaVcetneDph is null
""").show(false)

//navazanyZaznam
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end navazanyZaznam_check
from registry
where navazanyZaznam is null
""").show(false)

spark.sql("""
select
  case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry where navazanyZaznam is not null ), 4) * 100, "%)" " records are broken keys") end navazanyZaznam_check
from registry r1
left join registry r2 on r1.navazanyZaznam = r2.idSmlouvy
where r1.navazanyZaznam is not null and r2.idSmlouvy is null
""").show(false)

//predmet
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end predmet_check
from registry
where predmet is null
""").show(false)

//schvalil
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end schvalil_check
from registry
where schvalil is null
""").show(false)

//adresa
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end adresa_check
from registry
where adresa is null
""").show(false)

//datovaSchranka
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end datovaSchranka_check
from registry
where datovaSchranka is null
""").show(false)

spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end datovaSchranka_check
from registry
where length(datovaSchranka) <> 7
""").show(false)

//ico
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end ico_check
from registry
where ico is null
""").show(false)

spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are in wrong format") end ico_check
from registry
where length(ico) <> 8
""").show(false)

spark.sql("""
select
   lpad(ico, 8, "0")
from registry
where length(ico) <> 8
""").show(false)

//nazev
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end nazev_check
from registry
where nazev is null
""").show(false)

//platce
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end platce_check
from registry
where platce is null
""").show(false)

spark.sql("""
select
   cast(platce as boolean)
   , count(*)
from registry
group by 1
""").show(false)

//utvar
spark.sql("""
select
   case when count(*) = 0 then "OK" else concat("FAIL: ", count(*), "(", round(count(*) / (select count(*) from registry), 4) * 100, "%)", " records are null") end utvar_check
from registry
where utvar is null
""").show(false)

spark.sql("""
select
   count(distinct utvar)
from registry
where utvar is not null
""").show(false)


spark.sql("""
select
   id
   , idSmlouvy
   , idVerze
   , cast(casZverejneni as timestamp) casZverejneni
   , odkaz
   , cast(platnyZaznam as boolean) platnyZaznam
   , cisloSmlouvy
   , hodnota
   , upper(mena) mena
   , cast(datumUzavreni as timestamp) datumUzavreni
   , hodnotaBezDph
   , hodnotaVcetneDph
   , navazanyZaznam
   , predmet
   , schvalil
   , adresa
   , datovaSchranka
   , lpad(ico, 8 , "0") ico
   , nazev
   , cast(platce as boolean) platce
   , utvar
from registry
""").show()

spark.sql("""
select
  date_format(from_unixtime(unix_timestamp(cast(casZverejneni as timestamp))), "y-MM-dd'T'hh:mm:ss.SSS'Z'")
from registry
""").show(false)




//Describe table to see it`s contents
spark.sql("""describe contractParty""").show(1000, false)

//Construct reasonable id for SOLR
spark.sql("""
select
  concat(id, regexp_replace(nazev, "\\s", "_"), "_", row_number() over (partition by id, ico order by 1)) id
  , id parentId
  , adresa
  , datovaSchranka
  , lpad(ico, 8, "0") ico
  , nazev
  , cast(prijemce as boolean) prijemce
  , utvar
from contractParty
""").show()

//Construct reasonable id for SOLR
spark.sql("""
select
  distinct prijemce
from contractParty
""").show(false)


//Construct reasonable id for SOLR
spark.sql("""
select
*
from contractParty
having where id = "368781_392925"
""").show(false)


//Describe table to see it`s contents
spark.sql("""describe attachments""").show(1000, false)


//Construct reasonable id for SOLR
spark.sql("""
select
  id parentId
  , _algoritmus algoritmus
  , _VALUE hash
  , nazevSouboru
  , odkaz
from attachments
""").show()


//Construct reasonable id for SOLR
spark.sql("""
select
   count(*)
from attachments
""").show()
```

# Using SOLR for data indexing

## Using SolrJ library
To determine correct Solr API version we must first find out what SOLR version is deployed.
Since we use Cloudera CDH 5.13.1 we can consult the documentation at: https://www.cloudera.com/documentation/enterprise/release-notes/topics/cdh_vd_cdh_package_tarball_513.html#cm_vd_cdh_package_tarball_513

Download local SOLR installation from: http://archive.apache.org/dist/lucene/solr/4.10.3/solr-4.10.3.zip

Store it on local drive and extract it.

```
java -jar ./start.jar
```

Next we add dependency to POM:
```xml
<!-- https://mvnrepository.com/artifact/org.apache.solr/solr-solrj -->
<dependency>
	<groupId>org.apache.solr</groupId>
	<artifactId>solr-solrj</artifactId>
	<version>4.10.3</version>
</dependency>
```

Define `schema.xml` for SOLR index.<br />
```xml
...
   <field name="id" type="string" indexed="true" stored="true" required="true" multiValued="false" /> 
        
	<field name="idSmlouvy" type="long" indexed="true" stored="true" />
	<field name="idVerze" type="long" indexed="true" stored="true" />
	<field name="casZverejneni" type="date" indexed="true" stored="true" />
	<field name="odkaz" type="string" indexed="true" stored="true" />
	<field name="platnyZaznam" type="boolean" indexed="true" stored="true" />
	<field name="cisloSmlouvy" type="string" indexed="true" stored="true" />
	<field name="hodnota" type="float" indexed="true" stored="true" />
	<field name="mena" type="string" indexed="true" stored="true" />
	<field name="datumUzavreni" type="date" indexed="true" stored="true" />
	<field name="hodnotaBezDph" type="float" indexed="true" stored="true" />
	<field name="hodnotaVcetneDph" type="float" indexed="true" stored="true" />
	<field name="navazanyZaznam" type="long" indexed="true" stored="true" />
	<field name="predmet" type="text_cz" indexed="true" stored="true" />
	<field name="schvalil" type="string" indexed="true" stored="true" />
	<field name="adresa" type="string" indexed="true" stored="true" />
	<field name="datovaSchranka" type="string" indexed="true" stored="true" />
	<field name="ico" type="string" indexed="true" stored="true" />
	<field name="nazev" type="text_cz" indexed="true" stored="true" />
	<field name="platce" type="boolean" indexed="true" stored="true" />
	<field name="utvar" type="string" indexed="true" stored="true" />
	<field name="type" type="string" indexed="true" stored="true" />
	<field name="parentId" type="string" indexed="true" stored="true" />
	<field name="prijemce" type="string" indexed="true" stored="true" />
...

   <copyField source="cisloSmlouvy" dest="text"/>
   <copyField source="casZverejneni" dest="text"/>
   <copyField source="odkaz" dest="text"/>
   <copyField source="datumUzavreni" dest="text"/>
   <copyField source="hodnotaBezDph" dest="text"/>
   <copyField source="hodnotaVcetneDph" dest="text"/>
   <copyField source="hodnota" dest="text"/>
   <copyField source="mena" dest="text"/>
   <copyField source="predmet" dest="text"/>
   <copyField source="schvalil" dest="text"/>
   <copyField source="adresa" dest="text"/>
   <copyField source="datovaSchranka" dest="text"/>
   <copyField source="ico" dest="text"/>
   <copyField source="nazev" dest="text"/>
   <copyField source="utvar" dest="text"/>
...
```

Add to solrconfig.xml
```xml
<requestHandler name="/registry-app" class="solr.SearchHandler">
	<lst name="defaults">
       <str name="echoParams">explicit</str>
	   
	   

       <!-- VelocityResponseWriter settings -->
       <str name="wt">velocity</str>
       <str name="v.template">browse</str>
       <str name="v.layout">layout</str>
       <str name="title">KB - Registr smluv</str>

       <!-- Query settings -->
       <str name="defType">edismax</str>
       <str name="qf">
		text^0.5 predmet^5 ico nazev^8 cisloSmlouvy^0.5 casZverejneni odkaz datumUzavreni
		schvalil adresa^0.5 datovaSchranka utvar id^3 parentId^3
       </str>
       <str name="df">predmet</str>
       <str name="mm">100%</str>
       <str name="q.alt">*:*</str>
       <str name="rows">10</str>
       <str name="fl">*</str>

       <!-- Faceting defaults -->
       <str name="facet">on</str>
       <str name="facet.missing">true</str>
       <str name="facet.field">mena</str>
	   <str name="facet.field">platce</str>
	   <str name="facet.field">type</str>
       <str name="facet.mincount">1</str>
       <str name="facet.range">hodnotaBezDph</str>
	   <int name="f.hodnotaBezDph.facet.range.start">0</int>
       <int name="f.hodnotaBezDph.facet.range.end">10000000</int>
	   <int name="f.hodnotaBezDph.facet.range.gap">500000</int>
	   <str name="f.casZverejneni.facet.range.start">NOW/YEAR-10YEARS</str>
       <str name="f.casZverejneni.facet.range.end">NOW</str>
       <str name="f.casZverejneni.facet.range.gap">+1YEAR</str>
       <str name="f.casZverejneni.facet.range.other">before</str>
       <str name="f.casZverejneni.facet.range.other">after</str>
	   <str name="f.datumUzavreni.facet.range.start">NOW/YEAR-10YEARS</str>
       <str name="f.datumUzavreni.facet.range.end">NOW</str>
       <str name="f.datumUzavreni.facet.range.gap">+1YEAR</str>
       <str name="f.datumUzavreni.facet.range.other">before</str>
       <str name="f.datumUzavreni.facet.range.other">after</str>

       <!-- Highlighting defaults -->
       <str name="hl">on</str>
       <str name="hl.fl">predmet, nazev, ico</str>
       <str name="hl.preserveMulti">true</str>
       <str name="hl.encoder">html</str>
       <str name="hl.simple.pre">&lt;b&gt;</str>
       <str name="hl.simple.post">&lt;/b&gt;</str>

       <!-- Spell checking defaults -->
       <str name="spellcheck">on</str>
       <str name="spellcheck.extendedResults">false</str>       
       <str name="spellcheck.count">5</str>
       <str name="spellcheck.alternativeTermCount">2</str>
       <str name="spellcheck.maxResultsForSuggest">5</str>       
       <str name="spellcheck.collate">true</str>
       <str name="spellcheck.collateExtendedResults">true</str>  
       <str name="spellcheck.maxCollationTries">5</str>
       <str name="spellcheck.maxCollations">3</str>           
       
     </lst>

     <!-- append spellchecking to our list of components -->
     <arr name="last-components">
       <str>spellcheck</str>
     </arr>
  </requestHandler>
```


Add document test
```xml
<add>
	<doc>
		<field name="idSmlouvy">9</field>
		<field name="idVerze">9</field>
		<field name="casZverejneni">2016-07-01T01:17:36+02:00</field>
		<field name="odkaz">https://smlouvy.gov.cz/smlouva/9</field>
		<field name="cisloSmlouvy">12-OBJ/893/2016</field>
		<field name="datumUzavreni">2016-06-30</field>
		<field name="hodnotaBezDph">80000</field>
		<field name="predmet">rozšíření jádra IS Ginis o funkcionalitu zveřejňování smluv</field>
		<field name="schvalil">Ing. Pavel Tvrzník</field>
		<field name="adresa">U Jezu 642/2a, 46001 Liberec, CZ</field>
		<field name="datovaSchranka">sxk8tap</field>
		<field name="ico">70891508</field>
		<field name="nazev">Liberecký kraj</field>
	</doc>
</add>
```

## Creating core in SolrCloud
```bash
solrctl instancedir --generate ~/registry-app
solrctl instancedir --create registry-app ~/registry-app
solrctl collection --create registry-app -s 5 -c registry-app
```

## HBase
```
hbase(main):003:0> disable 'registry-attachments'
0 row(s) in 18.4210 seconds

hbase(main):001:0> alter 'registry-attachments', {NAME=>'data',COMPRESSION=>'gz'}
Updating all regions with the new schema...
242/242 regions updated.
Done.
0 row(s) in 2.5070 seconds

hbase(main):002:0> enable 'registry-attachments'
0 row(s) in 8.3320 seconds

hbase(main):003:0> major_compact 'registry-attachments'
0 row(s) in 1.0120 seconds
```


