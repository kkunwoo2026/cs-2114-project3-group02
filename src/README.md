## Reflection





## 6. Next Steps

Development will focus on strengthening the current prototype, maintaining MVC separation, and preparing for the final build.

### Development Priorities

| Priority | Focus | Planned Work |
|:---:|---|---|
| **1** | Testing & Reliability | Test queue operations, invalid inputs, duplicate users, room matching, and offer handling. |
| **2** | MVC Separation | Verify that the model operates independently of the view and that application logic remains in the controller. |
| **3** | Deliverable 4 | Implement one non-trivial feature beyond the current MVP. |
| **4** | Final Build | Complete testing, resolve remaining issues, update documentation, and prepare the final demonstration. |

### Proposed New Feature — Persistent Data

Allow HokieHold to save and reload relevant application information between runs.

**Anticipated design changes:**

- **New class:** A data-storage component to manage saving and loading.
- **ControllerMain:** Coordinate data storage with the application.
- **WaitingList:** Retain its existing queue operations.
- **ViewMain:** Remain responsible for console interaction without handling file storage.

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

- [ ] FIFO ordering and all core queue operations.
- [ ] Correct handling of invalid inputs and duplicate users.
- [ ] Room capacity matching and queue position preservation.
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
| **Robert** | |
| **Matthew** | |
| **Kunwoo** | |

All AI-generated suggestions were reviewed and adapted to the project's requirements. Team members are responsible for understanding their code and being able to explain their implementation during the grading session.
