* v0.4.10: Added version awareness of reqman: Knowning its own version

* v0.4.9: Fixed a templating bug which caused conditional fields to be useless.

* v0.4.8: Added a new expression in RMTL (isUnlocked[][])

* v0.4.7: Added auto-saving upon quitting with unsafed changes

* v0.4.6: Added the feature to modify a pre-existing group.

* v0.4.5: Added predecessors check: Progress cannot be tracked if not ALL predecessors have been achieved

* v0.4.4: Added global milestone choice (located under menu Edit)
Changed milestone choice: Choice upon selecting milestone (no more update button)
Removed update button to update the current milestone
Minor UI layout improvements

* v0.4.0: Merged editor and evaluator to reqman. There are no longer separate applications

* v0.3.8: Introduced a templating configuration system, both: editor and evaluator are useing

* v0.3.8-SNAPSHOT: Refractored percentage (fraction) calculation: Happening during
Progress.setPoints now)

Fixed an issue where unachieved requirements seemed to be achieved upon
exporting.

* v0.3.7-SNAPSHOT: Added opening multiple groups
Fixed progress summary not saving / loading
Added support for updating groups to updated catalogue
Added Ctrl+S for saving the currently active group

* v0.3.6-SNAPSHOT: Added a separation from 0 points (no progress) and 0 points (full progress, since requirment has 0 max points). Also enhanced the export

* v0.3.5-SNAPSHOT: Added visual indication of unsaved changes on the group.\nAdded error messages if any non-matching json file is loaded

* v0.3.4-SNAPSHOT: Added last open location. Evaluator remebers the directory of the last
opened group.
Added checks if the group name is unique and iff not, blocking the
actions until it is unique.
Changed warning dialogs related to opening group to error dialogs (which
is more accurate)
Fixed malus zero points displayed as -0 (now displayed as 0)

* v0.3.3-SNAPSHOT: Several minor improvemnts of the evaluator mode. Still a snapshot since the export is hardcoded

* v0.3.2-SNAPSHOT: Added 'remember last save location' feature

* v0.3.1-SNAPSHOT: Fixed: Sorting of progress list upon export

* v0.3.0-SNAPSHOT: Added (hardcoded) export of groups in evaluator application.

* v0.1.13-SNAPSHOT: SNAPSHOT: Editor can export (hardcoded template) a catalogue. The template is built using reqman's templating language

* v0.1.12: Added warning dialogs to inform a user about missing mandatory fields

* v0.1.11: Added feature: Requirement meta data are now saved when clicking outside the currently editing cell (Saved when focus lost)

* v0.1.10: Added Requirement Properties UI

* v0.1.9: Fixed comparator crash

* v0.1.8: Changed maxPoints restriction: It is possible to set maxPoints of a requirement to 0 now

* v0.1.5: CHANGED Requirement object: Milestone references are based on the ordinal now. Thus previous saved catalogues are not readable anymore

* v0.1.6: Added Save and Save As menu items as well as linked key-combinations.\n Added key-combinations to every menu item so far

* v0.1.7: Added sorting to catalogue export. Sorting is based on the following rule: minMS->Mandatory->Name

