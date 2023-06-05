# Garden System 2018
The garden system was initially implemented using standard Java Enterprise technologies (such as JavaFX/Swing, JSF web, EJB, and etc.), which followed a monolithic architecture. As the system complexity grew, I am currently undertaking the task of transforming it into a microservice-based architecture, specifically designed for mobile applications. My objective is to achieve a more decoupled and easily upgradable architecture, while also providing a modern front-end experience utilizing the Spring/Spring Boot and Angular/Ionic frameworks. <strong>This repository contains the majority of Garden's implementation which is deprecated and replaced by new implementation with new technologies, such as Spring Microservices, Angular, Ionic, and etc.</strong>

The following is the depecated monolithic architecture design of Garden system:

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/68e706a9-ce77-48da-938e-20b07785b631" width="600">

## Peony Office
PeonyOffice is a rich-client Java application which is supported by web services of RoseWeb. PeonyOffice uses of Netbeans platform embedded with JavaFX to present powerful GUI layer for remote users, i.e. accounting firms to process their daily business operations.

#### Employee Management
Support accounting firms' employee management which includes daily working progress report, timesheet, working activity logs, and privileges of using PeonyOffice.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/63173a19-e755-4929-afc2-30455e0db669" width="600">

#### Job Dispatcher
Supprt managers assign and keep track of daily working assignments and communication with remote employees. Also, it has ability to follow up the current status of specific assignments (or jobs) to employees.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/9cb34b4a-bfce-45a7-bfe3-c32875a8e9e8" width="600">

#### Search Engine
A powerful search engine support company-wide's tax-case and document search. It uses of Amazon SNS service to send notifications to customers by SMS text messages. It also support to export search result into various format files, such as MS-words or PDF.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/98471d63-8908-422b-87e5-b4b49f2cb682" width="600">

#### Talker
Peony Office provides an instant messenger, i.e. talker, so that employees may communicate with each other anytime and anywhere. It can keeps track of coversations for a limited-time period and search engine can also find the relevant conversation for specific taxation case.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/957fbb2c-71ba-4349-ad0c-9511bac2bc58" width="600">

#### Daily Business Operations
Peony Office supports regular daily busienss operations such as taxation case report, internal working forum with historical logs, customized printing format, Email, etc.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/6c19adcb-2271-4886-a981-13483b3246fb" width="600">

#### Archive and Backup/Restore
Peony Office provides a powerful document management utilites, achive the documents according to very customized way defined by users, build backup plans, schedule backups, and, in the worst case of original archived documents' crashing, Peony Office may restore everything.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/2a012143-eef1-45d8-b070-6ab9636a772a" width="600">

## Rose Web
RoseWeb is a standard business web portal which not only provide business offical web site but also provides web services endpoints to support the remote PeonyOffice execution. It was implemented with standard Java enterprise technologies, such as JSF, REST-WS, EJB, JPA, etc.

#### Online Business Management Site
Rose web serves not only for accounting business firms but also serve for their customers. According to the login credentials, Rose offers an online management on taxation cases for business or individual customers. For business, it may view, communicate, search its customers. For customers, they may checkout their taxation case status and communicate with the representatives of the accounting firms who take responsibility of their cases.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/6bfb365b-e842-45c1-910c-82004db90d24" width="600">

#### Online Integrated Email for Business
For small business firms, one of headache daily problems is "email" which has no management at all. Rose web provides a centralized to manage their company's emails and associate emails with specific taxation cases. It will be presented in Peony Office also.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/689b85c5-e898-4ef6-9e2e-d2470505b2db" width="600">

#### Online CMS
Rose web is also a simplified content management system for business to maintain their web pages for the public, especially critical seasonal taxation news and business related knowledge-base.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/a0733f4f-69b8-4b90-ab6b-72d653d36602" width="600">

## Documents & Screen Shots
For more information on Garden system, refer to documents and screen shots folders in this repo.
