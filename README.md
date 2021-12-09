# Flux Capacitor down spin (onderuit)
Basic example of a game application that uses Flux Capacitor.

## The setup
A docker compose file configures the setup.
Once everything is started (see below) the following is running:
- 1 Flux Capacitor instance at `http://localhost:8888`
- 2 instances of app
- 1 instance of web at `http://localhost:8090`
- Angular dev server at `http://localhost:4200`

The web instance's responsibilities are to forward commands and queries (posted to `/api/command` and `/api/query`
respectively) to Flux Capacitor. It also establishes the identity of the user (via JWT parsing).

All commands and queries are handled by the app instances, so business rules are applied only there.

## Build and run
To build, you need to have [Node.js](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)
and [Maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) installed locally.
To run you need [Docker](https://docs.docker.com/get-docker/).

Execute `./run.sh` for the initial run. This performs a Maven build, builds and launches the dockers configured in
`docker-compose.yml` and launches the frontend.

For later runs you're recommended to execute `./run-backend.sh` and / or `./run-frontend.sh` as they launch a little
faster.

## Troubleshooting
* For Linux users: Make sure that the user you are running the scripts with is a member of the `docker` user group.
