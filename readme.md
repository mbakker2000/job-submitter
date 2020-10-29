## Job Submitter

This application allows you to submit and query jobs. 
You can also cancel these jobs.

#### Run the application

This application expects JRE 11 at minimum. It's build and tested using Maven 3.6.3. 
Simply build the jar by using `mvn package` and `java -jar` the result.

#### Api Docs

After running, visit `localhost:8080/swagger-ui.html` for the basic API docs.
This should help you get familiar with the API.

#### Automatic testing

Utilizing Github Actions, this application is automatically build & tested on every commit.
It will also release a docker image to the GitHub image registry.
Checkout the `/.github/workflows` folder or the GitHub Actions webpage at https://github.com/JoranBergfeld/job-submitter/actions

