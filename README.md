# Orlando Walking Tours Android  [![Build Status](https://travis-ci.org/cforlando/orlando-walking-tours-android.svg?branch=master)](https://travis-ci.org/cforlando/orlando-walking-tours-android)
## About  
**Orlando Walking Tours** is an Android app allowing users to create customized walking tours of the various historic locations around Orlando. 

**Version** 1.0 (MVP)

**Data Source** 
The list of historic locations is currently stored [here](https://brigades.opendatanetwork.com/dataset/Orlando-Historical-Landmarks/hzkr-id6u).

**Other repositories:**  
- [Orlando Walking Tours for iOS](https://github.com/cforlando/orlando-walking-tours-ios)

## Installation
Project meta is defined in the gradle build files ([project](https://github.com/cforlando/orlando-walking-tours-android/blob/master/build.gradle), [app](https://github.com/cforlando/orlando-walking-tours-android/blob/master/app/build.gradle), [data module](https://github.com/cforlando/orlando-walking-tours-android/blob/master/data/build.gradle)) and the [version file](https://github.com/cforlando/orlando-walking-tours-android/blob/master/gradleProject/version.gradle)

## Development Consideration
This is a community project. Unless you are devoting serious time to supporting this app do not introduce features requiring long term support. Feel free to use proven patterns but do not introduce unnecessary complexity. For example, do not use Dagger or another overarching DI solution as most developers are not familiar with this architecture and will only be deterred from participating when they are unable to wire together simple dependencies. As always, adhere to principle of least privilege. Contain code to as few files as possible with the most restrictive access as possible. Do not try to optimize and tangle the code base purely to suit your needs. Always question if other (future) contributors will be able to build off of what you contribute.

## Contributing
We encourage anyone who is interested in contributing to Orlando Walking Tours to do so!  In order to ensure good code quality, there are some guidelines we would like to adhere to when contributing to this project. 
Here are a few:
1. Follow the code convention of the file. If files use different conventions, code to the convention of each file separately. Yes, this means read the entire (class) file before making changes to it.
2. Build and test all changes before requesting a pull. Don't be a brat.
3. Keep changes as small as possible and follow [Git documentation guidelines](http://chris.beams.io/posts/git-commit), it'll make you a better developer.

#### Process
1. Fork or pull the repository's master branch.
2. Find an outstanding or create a new ticket.
3. Estimate how much time it would take for you to complete the ticket. If it is more than 2 hours, consider deconstructing the ticket further.
4. Add an in progress tag to the ticket and update your current status on it during development.
3. Make changes covered by the ticket.
4. Commit and reference the ticket number somewhere in the commit message.
5. Make a pull request and reference the ticket number in the pull message.
6. If the Travis build fails cancel the pull request and address the failure then go back to 4.
7. Wait for code review and address rejection if necessary.
8. Close the ticket when a pull is made.

## License
[MIT](https://github.com/cforlando/orlando-walking-tours-android/blob/master/LICENSE)

## About Code for Orlando

Code for Orlando, a local Code for America brigade, brings the community together to improve Orlando through technology. We are a group of “civic hackers” from various disciplines who are committed to volunteering our talents to make a difference in the local community through technology. We unite to improve the way the community, visitors, and local government experience Orlando.
