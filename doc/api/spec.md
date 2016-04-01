
# IoT Semantic Registry API: Specification

 [TOC]  

## Introduction  
This API allows owners of IoT data sources to register semantic descriptions about their resources/devices, entities/objects on a linked-data platform. It also supports other CRUD methods for the management of the descriptions such as lookup, update and removal. In turn, the API  allows consumers of IoT data (applications, experimenters,...) to discover their sources through semantic querying using a SPARQL endpoint.  

## URL Structure  
The operations below will partly or wholly have the URL structure below:  

```  
http://{server_host}/srd/{endpoint_name}/{repository_id}/{resource_id}
```  

where  

* ***server_host***: is the IP address or hostname of the server. This will also include the port number.  
* ***endpoint_name***: name of the endpoint that is done. This can be either ```registry``` or ```sparql```.  
* ***repository_id***: is the ID of the target repository on the server. The server might have one or more repositories.  
* ***resource_id***: is the id of the IoT resource or entity.  

## Representations  
  
### RDF
For RDF descriptions, the server will be able to receive and send in several formats.  

A client can specify the format of a description to be sent or received using the header field in the HTTP request. The header fields concerned are "Content-Type" and "Accept". To specify a format, the corresponding internet media type must be used.  

| Format 		| filename extension| Internet Media Type   |
| ------------- |:-------------:| -----|
| JSON-LD      	| *.jsonld 	| application/ld+json |
| Notation3 (N3)| *.n3    	| text/n3 |
| RDF/XML 		|  *.rdf  	| application/rdf+xml |
| OWL/XML		|  *.owl	| application/owl+xml |
| TURTLE 		| *.ttl 	| text/turtle |
| 				|  			| application/turtle |
| 				|  			| application/rdf+turtle |

###Non-RDF  

In the case of non-RDF responses from SPARQL requests (e.g. SELECT), the following formats are accepted:

| Format 		| filename extension| Internet Media Type   |
| ------------- |:-------------:| -----|
| JSON      	| *.json | application/json |
| XML			| *.xml    |   application/xml |
| CSV 		|  *.csv   |    text/csv |
| TSV 		|  *.tsv   |    text/tab-separated-values |
| TXT 		| *.txt | text/plain |


## IoT Description management  

### Register  

To register a description, the URL and body are set as follows:  
```
POST /srd/registry/{repository_id} HTTP/1.1
Host: {server_host}
Accept: {internet_media_type}
```
either  
```  
http://{server_host}/srd/registry/{repository_id}/{resource_id}
``` 
for one IoT Resource, or  
```  
http://{server_host}/srd/registry/{repository_id}
```  
for multiple IoT Resources.  

The body of the request would be an RDF model (e.g a sensing device), which can be in JSON-LD,  N3, TURTLE, or RDF/XML format:  

```json 
    {
  "@context": {
    "geo": "http://www.w3.org/2003/01/geo/wgs84_pos#",
    "iot-lite": "http://purl.oclc.org/NET/UNIS/fiware/iot-lite#",
    "j.0": "http://purl.oclc.org/NET/UNIS/iot-lite#",
    "j.1": "http://www.w3.org/2001/",
    "owl": "http://www.w3.org/2002/07/owl#",
    "qu": "http://purl.org/NET/ssnx/qu/qu#",
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "ssn": "http://www.w3.org/2005/Incubator/ssn/ssnx/ssn#",
    "time": "http://www.w3.org/2006/time#",
    "xsd": "http://www.w3.org/2001/XMLSchema#"
  },
  "@graph": [
    {
      "@id": "j.0:LocationDesk1",
      "@type": "geo:Point",
      "j.0:RelativeLocation": "Desk1"
    },
    {
      "@id": "j.0:SensingDevice1Service",
      "j.0:endpoint": "http://iot.ee.surrey.ac.uk/testbed/SensingDevice1Service"
    },
    {
      "@id": "j.0:CoverageDesk1",
      "j.0:hasPoint": {
        "@id": "j.0:LocationDesk1"
      }
    },
    {
      "@id": "j.0:Desk1SensingDevice1",
      "@type": "ssn:SensingDevice",
      "j.0:hasLocation": {
        "@id": "j.0:LocationDesk1"
      },
      "j.0:hasQuantityKind": {
        "@id": "qu:temperature"
      },
      "j.0:isExposedBy": {
        "@id": "j.0:SensingDevice1Service"
      }
    }
  ]
}

``` 
                
### Lookup  

To retrieve a description, the header and URL of the request are set as follows:  

```  
GET /srd/registry/{repository_id}/{resource_id} HTTP/1.1
Host: {server_host}
Accept: {internet_media_type}
```  
```  
http://{server_host}/srd/registry/{repository_id}/{resource_id}
```  

The body of the response  would be an RDF model (e.g a sensing device), which can be in JSON-LD,  N3, TURTLE, or RDF/XML:  

	@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#> .
	@prefix iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#> .
    @prefix qu:    <http://purl.org/NET/ssnx/qu/qu#> .
    @prefix owl:   <http://www.w3.org/2002/07/owl#> .
    @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix xml:   <http://www.w3.org/XML/1998/namespace> .
    @prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .
    @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
    @prefix time:  <http://www.w3.org/2006/time#> .
    @prefix ssn:   <http://www.w3.org/2005/Incubator/ssn/ssnx/ssn#> .
    
    <http://purl.oclc.org/NET/UNIS/iot-lite#LocationDesk1>
            a       geo:Point ;
            <http://purl.oclc.org/NET/UNIS/iot-lite#RelativeLocation>
                    "Desk1" .
    
    <http://purl.oclc.org/NET/UNIS/iot-lite#SensingDevice1Service>
            <http://purl.oclc.org/NET/UNIS/iot-lite#endpoint>
                    "http://iot.ee.surrey.ac.uk/testbed/SensingDevice1Service" .
    
    <http://purl.oclc.org/NET/UNIS/iot-lite#Desk1SensingDevice1>
            a                              ssn:SensingDevice ;
            <http://purl.oclc.org/NET/UNIS/iot-lite#hasLocation>
                    <http://purl.oclc.org/NET/UNIS/iot-lite#LocationDesk1> ;
            <http://purl.oclc.org/NET/UNIS/iot-lite#hasQuantityKind>
                    qu:temperature ;
            <http://purl.oclc.org/NET/UNIS/iot-lite#isExposedBy>
                    <http://purl.oclc.org/NET/UNIS/iot-lite#SensingDevice1Service> ;
            <http://www.w3.org/2001/vcar>  <http://www.w3.org/2001/vcar> .
    
    <http://purl.oclc.org/NET/UNIS/iot-lite#CoverageDesk1>
            <http://purl.oclc.org/NET/UNIS/iot-lite#hasPoint>
                    <http://purl.oclc.org/NET/UNIS/iot-lite#LocationDesk1> .


### Update  

To update a description, the header and URL of the request are set as follows:  

``` 
PUT /srd/registry/{repository_id}/{resource_id} HTTP/1.1
Host: {server_host}
Accept: {internet_media_type}
```  
```  
http://{server_host}/srd/registry/{repository_id}/{resource_id}
```  
The body of the request would be an RDF model (e.g a sensing device moved to another location), which can be in JSON-LD,  N3, TURTLE, or RDF/XML:   

```  
    @prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#> .
    @prefix iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#> .
    @prefix qu:    <http://purl.org/NET/ssnx/qu/qu#> .
    @prefix owl:   <http://www.w3.org/2002/07/owl#> .
    @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix xml:   <http://www.w3.org/XML/1998/namespace> .
    @prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .
    @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
    @prefix time:  <http://www.w3.org/2006/time#> .
    @prefix ssn:   <http://www.w3.org/2005/Incubator/ssn/ssnx/ssn#> .
    
    <http://purl.oclc.org/NET/UNIS/iot-lite#Desk1SensingDevice1>
        a                              ssn:SensingDevice ;
        <http://purl.oclc.org/NET/UNIS/iot-lite#hasLocation>
                <http://purl.oclc.org/NET/UNIS/iot-lite#LocationDesk2> ;
        <http://purl.oclc.org/NET/UNIS/iot-lite#hasQuantityKind>
                qu:temperature ;
        <http://purl.oclc.org/NET/UNIS/iot-lite#isExposedBy>
                <http://purl.oclc.org/NET/UNIS/iot-lite#SensingDevice1Service> ;
                <http://purl.oclc.org/NET/UNIS/iot-lite#SensingDevice1Service>
        <http://purl.oclc.org/NET/UNIS/iot-lite#endpoint>
                "http://iot.ee.surrey.ac.uk/testbed/SensingDevice1Service" .
                <http://purl.oclc.org/NET/UNIS/iot-lite#LocationDesk2>
        a       geo:Point ;
        <http://purl.oclc.org/NET/UNIS/iot-lite#RelativeLocation>
                "Desk2" .
                
```
### Remove  

To remove a description, the header and URL of the request are set as follows:  

```  
DELETE /srd/registry/{repository_id}/{resource_id} HTTP/1.1
Host: {server_host}
Accept: {internet_media_type}
```  
```  
http://{server_host}/srd/registry/{repository_id}/{resource_id}
```  

The body of the response  would be ....(RDF or non-RDF?)

    ...TODO

## IoT Discovery  
### Query  

To query for information from the semantic repository, you can append a SPARQL query using two options.  

#### Option 1: SPARQL query embedded in URL  

```  
GET /srd/sparql/{repository_id} HTTP/1.1
Host: {server_host}
Accept: {internet_media_type}
```  
```  
http://{server_host}/srd/sparql/{repository_id}?query={sparql_query}
```  
Please note that:  

 - the ```sparql_query``` must be url-encoded  
 - the number of characters in a URL must be less than 255.  

For example, a query like this:

```  
PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
SELECT *
WHERE {
  ?resource a ssn:SensingDevice.
}order by asc(UCASE(str(?s)))
``` 
would become...  
```  
PREFIX%20iot-lite%3A%20%3Chttp%3A%2F%2Fpurl.oclc.org%2FNET%2FUNIS%2Ffiware%2Fiot-lite%23%3E%0APREFIX%20ssn%3A%20%3Chttp%3A%2F%2Fpurl.oclc.org%2FNET%2Fssnx%2Fssn%23%3E%0ASELECT%20*%0AWHERE%20%7B%0A%20%20%3Fresource%20a%20ssn%3ASensingDevice.%0A%7Dorder%20by%20asc(UCASE(str(%3Fs)))
```   
An online tool can be used for encoding, such as [this](http://meyerweb.com/eric/tools/dencoder/) one.

The URL will then become:

``` 
http://{server_host}/srd/sparql/{repository_id}?query=PREFIX%20iot-lite%3A%20%3Chttp%3A%2F%2Fpurl.oclc.org%2FNET%2FUNIS%2Ffiware%2Fiot-lite%23%3E%0APREFIX%20ssn%3A%20%3Chttp%3A%2F%2Fpurl.oclc.org%2FNET%2Fssnx%2Fssn%23%3E%0ASELECT%20*%0AWHERE%20%7B%0A%20%20%3Fresource%20a%20ssn%3ASensingDevice.%0A%7Dorder%20by%20asc(UCASE(str(%3Fs)))
```  


#### Option 2: SPARQL query embedded in request body  

```  
POST /srd/sparql/{repository_id} HTTP/1.1  
Host: {server_host}  
Accept: {internet_media_type}  
```  

An example of the body of the request can be as follows:  
``` 

    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    PREFIX owl: <http://www.w3.org/2002/07/owl#>
    PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX ssn: <http://www.w3.org/2005/Incubator/ssn/ssnx/ssn#>
    prefix qu-rec20: <http://purl.org/NET/ssnx/qu/qu-rec20#>
    prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>
    prefix iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
    prefix iot-l-Ins: <http://purl.oclc.org/NET/UNIS/fiware/iot-liteInstance#>
    
    SELECT ?sensDev ?endp
    WHERE {
        ?sensDev iot-lite:hasQuantityKind qu-rec20:temperature;
        iot-lite:isExposedBy ?serv;
        iot-lite:hasCoverage ?cover.
        ?cover iot-lite:hasPoint ?point.
        ?point iot-lite:RelativeLocation ""Desk2"".
        ?serv iot-lite:endpoint ?endp. 
    }

```  

