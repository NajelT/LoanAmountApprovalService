## Build and run 

### Docker and docker-compose

You can run the service in containers. 

#### Prerequisites

-git \
-Docker \
-docker-compose 

#### Installation

Clone the repo:

```
git clone git@github.com:NajelT/LoanAmountApprovalService.git
```

cd to a target directory, where service code was cloned: 

````
cd LoanAmountApprovalService
````

And run container:

```
docker-compose up -d
``` 

Build will be done automatically, once container will up and running, service will be ready. 
By default, an application listening port 8080, so it should be available on `http://localhost:8080`
 
