# Skeditor

#####How to set up:

1. Install FeatureIDE and Xtext for Eclipse Modeling Tools
2. Clone this repository
3. Import projects:
    - de.tubs.skeditor
    - de.tubs.skeditor.examples
    - de.tubs.skeditor.spl
    - de.tubs.skeditor.spl.featuremap
    - de.tubs.skeditor.spl.featuremap.ide
    - de.tubs.skeditor.spl.featuremap.test
    - de.tubs.skeditor.spl.featuremap.ui
    - de.tubs.skeditor.spl.featuremap.ui.test
4. Follow readme instructions in:
    - de.tubs.skeditor/libs/readme.txt
    - de.tubs.skeditor.spl/libs/readme.txt
5. Open file "de.tubs.skeditor/model/SkillGraph.genmodel"
    - Right click on "SkillGraph" and select "Generate Model Code"
6. Open path to "de.tubs.skeditor.spl.featuremap/src/de.tubs.skeditor.spl"
    - Run "FeatureMap.xtext" as "Generate Xtext Artifacts"
7. Run "de.tubs.skeditor" project as "Eclipse Application" after errors resolve
