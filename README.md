


# GalleryKit   
[![](https://jitpack.io/v/chirag-ji/GalleryKit.svg)](https://jitpack.io/#chirag-ji/GalleryKit)  [![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
  
GalleryKit is **simplest / beautiful and smartest** gallery picker  
  
- Support Images & Videos
- Support Multiple selecttions
- Fully Configurable
  
## Setup  
  
### Gradle  

Add `jitpack` to `build.gradle (Project level)` 
```css
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add `dependency` to `build.gradle (app level)`
```css
dependencies {
	...
	implementation 'com.github.chirag-ji:GalleryKit:1.0.3'
	...
}
```

## Integration

#### Layout
```xml
	...
    	<com.github.chiragji.gallerykit.GalleryKitView  
		android:id="@+id/galleryKitView"
		android:layout_width="match_parent" 
		android:layout_height="match_parent"
		app:maxImageSelections="3"
		app:viewStyle="separate" />
	...
```
##### AppActivity extends AppCompactActivity
```java
protected void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    setContentView(R.layout.activity_main);  
    ...
    GalleryKitView galleryKitView = findViewById(R.id.galleryKitView);  
    galleryKitView.attachToFragmentActivity(this);  
    galleryKitView.registerGalleryKitListener(new GalleryKitListener() {  
    
   	@Override  
   	public void onGalleryKitBackAction() {  
            Log.d(TAG, "onBackKeyPressed: back key pressed on gallery kit");  
        }  
  
    	@Override  
    	public void onGalleryKitSelectionConfirmed(@NonNull List<String> selectedDataUris) {  
    	    Log.d(TAG, "onSelectionConfirmed: selectedDataUris.size = " + selectedDataUris.size());  
    	    selectedDataUris.forEach(selectedUri ->  
    	            Log.d(TAG, "onSelectionConfirmed: selectedUri = " + selectedUri));  
    	}  
    });
    ...
}
```
### Apply Selected Images to kit
```java
protected  void  onCreate(Bundle savedInstanceState)  {
	...
	GalleryKitView galleryKitView = findViewById(R.id.galleryKitView);
	galleryKitView.attachToFragmentActivity(this);
	galleryKitView.setSelectedData(dataList);
	...
}
```


### Customizations

 -  #####  redirects for single view (images and videos are shown in one tab)
 ```xml
	...
	app:combinedMaxSelections="<count>"
	app:viewStyle="combined"
	...
```
 -  #####  redirects for separate view (images and videos are shown in separate tabs)
 ```xml
	...
	app:maxImageSelections="<count>"  
	app:maxVideoSelections="<count>"
	app:viewStyle="separate"
	...
```
 -  #####  redirects for images only view
 ```xml
	...
	app:maxImageSelections="<count>"
	app:viewStyle="imageOnly"
	...
```
 -  #####  redirects for images only view
 ```xml
	...
	app:maxVideoSelections="<count>"
	app:viewStyle="videoOnly"
	...
```
 -  ##### disable top bar showing selected images / videos
 ```xml
	 ...
	 app:showSelectedResources="false"
	 ...
```
 -  ##### change back button image
 ```xml
	 ...
	 app:backButtonImageSrc="@drawable/ic_clear"
	 ...
```
   -  ##### hide back button
 ```xml
	 ...
	 app:hideBackButton="true"
	 ...
```
   -  ##### change done button color
 ```xml
	 ...
	 app:doneButtonColor="@color/colorAccent"
	 ...
```


***

***More features will come soon***

***

If you think this library is useful, please press star button at upside. </br>  
<img src="https://phaser.io/content/news/2015/09/10000-stars.png" width="200">  

## LICENCE
I don't care about any licensing, keen to accept all contributions and making it the most trusted, beautiful and smartest library :)
