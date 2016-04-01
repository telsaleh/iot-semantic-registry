
# IoT Semantic Registry API: Example Usage

##Introduction  
This document will provide an example on how to use the IoT Semantic Registry API.  

## IoT Resource/Entity Descriptions

### Model  
To make use of this API, your IoT description must be annotated so that it conforms to the FIESTA-IoT ontology.  

![FIESTA ontology](https://www.dropbox.com/s/uv6yflm7hg7wwpb/FIESTA-ontologies-v0_3_overview.png?dl=1)   

### Identification  

For identification of Instances (or Individuals) of Resources and Entities, dereferenceable URIs should be used, meaning that they act as a valid URLs which exposes a web resource, which in this case is the IoT resource description.  The URI will have a structure as specified in the IoT Semantic Registry specification.  

For example, if we have an IoT resource with an ID ```IoT-Node-001``` that is to be registered at a Semantic Registry at ```http://platform.fiesta-iot.eu/srd/registry/``` with a repository name **```myrepo```** (for now any repo name can be used), then its URI will be a combination of the Semantic Registry's host name and path, and the resource ID at the end.  This becomes:

```http://platform.fiesta-iot.eu/srd/registry/myrepo/IoT-Node-001  ```   

### Prefixes  
The prefixes to used for the fiesta ontology are:

```
iot-lite: 	http://purl.oclc.org/NET/UNIS/fiware/iot-lite#
m3-lite: 	http://purl.org/iot/vocab/m3-lite#
ssn:   		http://purl.oclc.org/NET/ssnx/ssn#
qu:    		http://purl.org/NET/ssnx/qu/qu#
geo:   		http://www.w3.org/2003/01/geo/wgs84_pos#
time:  		http://www.w3.org/2006/time#
fiesta-res: http://platform.fiesta-iot.eu/srd/registry/{repo_name}/
```
RDF-specific: 
```
owl:   http://www.w3.org/2002/07/owl#
xsd:   http://www.w3.org/2001/XMLSchema#
rdfs:  http://www.w3.org/2000/01/rdf-schema#
rdf:   http://www.w3.org/1999/02/22-rdf-syntax-ns#
xml:   http://www.w3.org/XML/1998/namespace

```   

###Example of An IoT Resource Description

As an example of a sensor device, let's take the SmartCCSR IoT Node.

![FIESTA ontology](https://www.dropbox.com/s/9lo9ezv7qt8131i/smartccsr-iot-node.png?dl=1)

The model for such a device may look like this:  

![IoT-node-model](https://www.dropbox.com/s/fd9eq79odq65o3v/smartccsr-iot-node-model.png?dl=1)

For one of the sensing devices, the model make look like this:  

![FIESTA ontology](https://www.dropbox.com/s/i1a4fl8tv9fzqnq/smartccsr-iot-node-Resource.png?dl=1)  
  
This can be represented in an RDF variant (e.g. JSON-LD) as:  
  
```  
{
  "@context": {
    "fiesta-res": "http://platform.fiesta-iot.eu/srd/registry/v1/",
    "geo": "http://www.w3.org/2003/01/geo/wgs84_pos#",
    "iot-lite": "http://purl.oclc.org/NET/UNIS/fiware/iot-lite#",
    "m3-lite": "http://purl.org/iot/vocab/m3-lite#",
    "owl": "http://www.w3.org/2002/07/owl#",
    "qu": "http://purl.org/NET/ssnx/qu/qu#",
    "qu-rec20": "http://purl.org/NET/ssnx/qu/qu-rec20#",
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "sc": "http://smartcampus.iot.ee.surrey.ac.uk/smart-ics#",
    "ssn": "http://purl.oclc.org/NET/ssnx/ssn#",
    "time": "http://www.w3.org/2006/time#",
    "xsd": "http://www.w3.org/2001/XMLSchema#"
  },
  "@graph": [
    {
      "@id": "sc:SensingDevice1Service",
      "@type": "iot-lite:Service",
      "iot-lite:endpoint": "http://131.227.92.112:8080/SmartCCSR-testbed/restful-services/REDUCE/json/sensors/1SensingDevice1Service"
    },
    {
      "@id": "sc:ICS-Desk1",
      "@type": "ssn:Platform",
      "geo:location": {
        "@id": "sc:CII-UNIS-GU2-UK-ICS-Desk1"
      }
    },
    {
      "@id": "sc:CII-UNIS-GU2-UK-ICS-Desk1",
      "@type": "geo:Point",
      "geo:lat": "51.4",
      "geo:long": "-0.51",
      "iot-lite:RelativeLocation": "ICS-Desk1"
    },
    {
      "@id": "fiesta-res:IoT-Node1",
      "@type": "ssn:Device",
      "ssn:hasSubSystem": {
        "@id": "fiesta-res:IoT-Node1TEMPERATURE"
      }
    },
    {
      "@id": "fiesta-res:IoT-Node1TEMPERATURE",
      "@type": "ssn:SensingDevice",
      "iot-lite:hasQuantityKind": {
        "@id": "m3-lite:Temperature"
      },
      "iot-lite:isExposedBy": {
        "@id": "sc:SensingDevice1Service"
      },
      "iot-lite:isSubSystemOf": {
        "@id": "sc:smart-ics"
      },
      "ssn:onPlatform": {
        "@id": "sc:ICS-Desk1"
      }
    },
    {
      "@id": "sc:SmartCampus",
      "@type": "ssn:System"
    },
    {
      "@id": "sc:smart-ics",
      "@type": "ssn:System",
      "iot-lite:isSubSystemOf": {
        "@id": "sc:SmartCampus"
      }
    }
  ]
}  
```  
  
## Registering an IoT Resource or Entity  

 To register a Resource, the request must:
  
 - use the HTTP **POST** method 
 - have the **content-type** and **accept** headers populated with the **format** of the description to be registered and the response respectively. The content-type will assist the Semantic Registry in using the right parser for the RDF variant.
 - The **URL** of the request will be the same as the **URI** of the IoT Resource, as explained in the previous section.

```
POST /srd/registry/myrepo HTTP/1.1
Host: platform.fiesta-iot.eu
Content-Type: application/ld+json
Accept: application/json
```  
The body of the request must include the description of a Resource(s)  using the acceptable RDF serialization formats.  
    
### Example using REST Client    
    
  ![rest-client-register](https://www.dropbox.com/s/6mfqxn2a8odww4c/srd-register-request.png?dl=1)

## Querying an IoT resource or entity  

### Example using REST Client

![rest-client-sparql](https://www.dropbox.com/s/y2rjwbqasqpk3w1/srd-sparql-request.png?dl=1)

### Query 1: Resource description in  RDF format (same as Registry Retrieve operation)
```  
PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>

DESCRIBE <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#Desk9SensingDevice5>
```  
###  Query 2: All "Resources"
```  
PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
SELECT *
WHERE {
  ?resource a ssn:SensingDevice.
}order by asc(UCASE(str(?s)))
```  

### Query 3: All "Resources" measuring a particular phenomenon (e.g. Temperature)  

```  
PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
PREFIX m3-lite: <http://purl.org/iot/vocab/m3-lite#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
SELECT *
WHERE {
  ?resource a ssn:SensingDevice.
  ?resource iot-lite:hasQuantityKind <http://purl.org/iot/vocab/m3-lite#Temperature>
}order by asc(UCASE(str(?s)))
```  

### Query 4: All Resources within a geographical area (bounding box) 
 
  
```  
PREFIX geo:  <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

SELECT ?dev ?resource ?lat ?long
WHERE { 
    ?dev a ssn:Device .
    ?dev ssn:hasSubSystem ?resource.
    ?resource a ssn:SensingDevice.
    ?dev ssn:onPlatform ?platform .
    ?platform geo:location ?point .
    ?point geo:lat ?lat .
    ?point geo:long ?long .
    FILTER ( 
       (xsd:double(?lat) >= "0"^^xsd:double) 
    && (xsd:double(?lat) <= "60"^^xsd:double) 
    && ( xsd:double(?long) < "100"^^xsd:double)  
    && ( xsd:double(?long) > "-0.60"^^xsd:double)
    )     
}
```  
### Query 5: Resources within a geographical area (bounding box)  measuring certain phenomena
``` 
PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>
PREFIX m3-lite: <http://purl.org/iot/vocab/m3-lite#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
PREFIX geo:  <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT  ?dev ?qk ?unit ?lat ?long ?endp 
WHERE {
  ?dev a ssn:Device .
  ?dev ssn:onPlatform ?platform .
  ?platform geo:location ?point .
  ?point geo:lat ?lat .
  ?point geo:long ?long .
  ?dev ssn:hasSubSystem ?sensor .
  ?sensor a ssn:SensingDevice .
  ?sensor iot-lite:exposedBy ?serv .
  ?sensor iot-lite:hasQuantityKind ?qk .
   VALUES ?qk {m3-lite:AirTemperature m3-lite:Presence}
  ?sensor iot-lite:hasUnit ?unit .
  ?serv iot-lite:endpoint ?endp .
  FILTER ( 
       (xsd:double(?lat) >= "0"^^xsd:double) 
    && (xsd:double(?lat) <= "60"^^xsd:double) 
    && ( xsd:double(?long) < "10"^^xsd:double)  
    && ( xsd:double(?long) > "-6"^^xsd:double)
    )     
}order by asc(UCASE(str(?qk)))

 
```  