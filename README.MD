
## CV Parser

#### Requirements: 
- SECRET_KEY - environment variable:  
<em> any value eg "DummyKey" is it is used to crypt and decrypt consultants name and surname.
The most important thing is to use the same key for reading and saving data - if you change key remeber to drop db.</em>
- Running container:  
<em> from root folder use docker compose to start container with dynamodb </em>  
```bash
docker compose up
```