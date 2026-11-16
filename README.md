## How to run the model
Run ViewMain as java and follow through the instructions in the console. 
By inserting the name and HokiePID, users will be able to reserve, take, and leave study rooms based on the number of the party.

## Reflection

### 1. Changes from the Specification
Our implementation stayed very close to the original specification. We spent a significant amount of time developing detailed method stubs and defining the responsibilities of each class before beginning implementation. As a result, most of our implementation work involved completing the methods that we had already planned rather than changing the original design.

One implementation detail was the use of a contains() method in WaitingList to prevent duplicate requests from the same user. We also used HashSet and HashMap to organize requests with different party sizes and direct them to their corresponding waiting queues. These data structures helped us manage requests more efficiently.

Overall, the original class structure and method responsibilities remained largely unchanged. Most of our decisions during implementation followed the original design.

What changed from our D3 plan: Since D3 we wrote JUnit tests for Room, WaitingList, and User, and we started the save feature. 

### 2. Model Independence
Our model was designed to operate independently of the view. The model classes manage their own data and responsibilities without directly handling console input or output.

The User, Room, and WaitingList classes handle user information, room reservations, and waiting-list operations. The controller coordinates these classes and communicates the results to the view.

We did not need to move the model's core responsibilities into the view to make the application work. However, the model has not yet undergone independent testing, so its independence still needs to be verified through JUnit tests.

### 3. Scope Unknowns
One of the challenges we addressed was preventing duplicate reservation requests. We implemented a contains() method in WaitingList to check whether a user was already in a queue before accepting another request.

We also figured out how to manage requests with different party sizes. By using HashSet and HashMap, we organized requests by the number of people in the party. We made different queues and sent the request to the appropriate queue.

### 4. How we are implementing changes, prototype #2
For this deliverable we are implementing a save feature, so HokieHold can save its data and reload it between runs. This is the Persistent Data feature we named in our D3 Next Steps. As we predicted in D3, the feature is a new data-storage class that ControllerMain coordinates. WaitingList keeps its existing queue operations, and ViewMain stays responsible only for console input and output, with no file handling.

So far the change has not crossed the model/view line. The view does not need to know how saving works, because saving goes through the controller and the storage class.

### 5. What Does Not Work Yet?
The save feature is still being implemented, so saving and loading is not finished or fully tested yet. Once it works, we will add JUnit tests for the new model methods, including a bad-input case for each.

We plan to continue working on the user interface and better prompt when there is a bad input

## Next Steps

Development will focus on finishing the save feature, strengthening the current prototype, and maintaining MVC separation for the final build.

### Development Priorities

| Priority | Focus | Planned Work |
|:---:|---|---|
| **1** | Save feature | 	Finish saving and loading data between runs through a new storage class coordinated by ControllerMain. |
| **2** | MVC Separation | Verify that the model operates independently of the view and that application logic remains in the controller. |
| **3** | Testing & Reliability | Implement JUnit test for the save feature. |
| **4** | Final Build | Complete testing, resolve remaining issues, update documentation, and prepare the final demonstration. |

### Stretch Goals 
- Continuing to build on the save feature and get it into a spot that we are satisfied with
- Continuing to improve on the UI and make it better for the user 

**Anticipated design changes:**
- **New class:** A data-storage component to manage saving and loading.
- **ControllerMain:** Coordinate data storage with the application.
- **WaitingList:** Retain its existing queue operations.
- **ViewMain:** Remain responsible for console interaction without handling file storage.

**3-minute Demo** 
We will start the program, reserve a room, join a waiting list, and accept a room offer. Then we will show the save feature by saving, restarting, and showing that the data reloaded. We will finish with a bad input being rejected without a crash.

### Task Ownership

| Team Member | Assigned Responsibilities |
|---|---|
| **Zachary** | Controller logic, room matching, and console interface |
| **Robert** | WaitingList implementation and testing |
| **Matthew** | Room implementation and testing |
| **Kunwoo** | User implementation and testing |
| **Entire Team** | Integration, system-wide testing, and final demonstration |

### Testing Goals

Before the final submission, the team plans to verify:

- [Done] FIFO ordering and all core queue operations.
- [Done] Correct handling of invalid inputs and duplicate users.
- [Done] Room capacity matching and queue position preservation.
- [ ] Successful acceptance, declining, and reassignment of room offers.
- [ ] Model independence from the view.
- [ ] Correct saving and loading if the proposed feature is implemented.

---

## Work Attribution

The following table identifies each team member's primary contributions to the first prototype.

| Team Member | Classes / Components | Contributions |
|---|---|---|
| **Zachary** | `ControllerMain.java`, `ViewMain.java` | Implemented controller functionality, room matching, queue coordination, and the console interface. |
| **Robert** | `WaitingList.java` | Developed the custom linked queue and its core operations. |
| **Matthew** | `Room.java` | Implemented room information, availability, and offer/reservation management. |
| **Kunwoo** | `User.java` | Implemented student information and user identification. |
| **Entire Team** | Integration | Collaborated on combining components and reviewing overall application functionality. |

---

## Generative AI Usage

Generative AI was used as a development aid throughout the project. The following table describes each member's use of AI.

| Team Member | Generative AI Usage |
|---|---|
| **Zachary** | Used AI to discuss design decisions and debug controller and view functionality. |
| **Robert** | Did not use AI during the creation of the prototype. |
| **Matthew** | Used AI to help with design plans and account for bad inputs|
| **Kunwoo** | Didn't use AI during the creation of User class. |

All AI-generated suggestions were reviewed and adapted to the project's requirements. Team members are responsible for understanding their code and being able to explain their implementation during the grading session.
