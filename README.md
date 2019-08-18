# AdLibrary
AdsNative is the type of Ad where you can pass your own views which includes Ad Assets just like AdMob's NativeAdvancedUnified.

The setNativeAdView() method in AdsNative accepts two types of object to specify your View containing Ad Assets.
  
	AdsNativeAdView Object,
	View Object containing Ad Assets.
	
AdsNativeView
	
	If you use a AdsNativeView, you'll need to pass the ids of the Assets (Icon, Call to Action View, Header Image etc) in a AdsNativeView in their respective setter methods and then set that object to the AdsNative's setNativeView() . 
	Following is an example of AdsNativeView -
	
	final Relativelayout adLayout = findViewById(R.id.adLayout); //Ad Assets inside a ViewGroup
	adlayout.setVisibility(View.GONE):
	AdsNativeView nativeView = new AdsNativeView();
	nativeView.setTitleView((TextView) findViewById(R.id.appinstall_headline));
	nativeView.setDescriptionView((TextView) findViewById(R.id.appinstall_body));
	nativeView.setIconView((ImageView) findViewById(R.id.appinstall_app_icon));
	nativeView.setHeaderImageView((ImageView) findViewById(R.id.large));
	nativeView.setCallToActionView(findViewById(R.id.appinstall_call_to_action));
	nativeView.setPriceView((TextView) findViewById(R.id.price));
	nativeView.setRatingsView((RatingBar) findViewById(R.id.rating));
	
Passing a View object in AdsNative
	
	You can also pass a View in the setNativeAdView(), however there are some rules you'll need to follow! 
	You'll need to use the same IDs for your Ad Assets mentioned below -
	Ad Assets										IDs
	Header Image								houseAds_header_image
	App Icon										houseAds_app_icon
	Title												houseAds_title
	Description									houseAds_description
	Price												houseAds_price
	RatingBar										houseAds_rating

Loading AdsNative
	
	AdsNative adsNative = new AdsNative(NativeAdActivity.this, adUrl);
	adsNative.setNativeAdView(nativeView); //AdsNativeView Object
	adsNative.setNativeNativeView(adLayout); //View Object
	adsNative.setNativeAdListener(new NativeAdListener() {            
    @Override
    public void onAdLoaded() {
        adLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAdLoadFailed() {
        Toast.makeText(NativeAdActivity.this, "Failed", Toast.LENGTH_SHORT).show();
    }
	});
	adsNative.loadAds();
 
	Check if NativeAd is loaded - adsNative.isAdLoaded(); 
	Additionally, you can define your own 'Call to Action' Button's action by using a CallToActionListener, for e.g.
	adsNative.setCallToActionListener(new NativeAdListener.CallToActionListener() {
            @Override
            public void onCallToActionClicked(View view) {
                //Do your Stuff!
            }
        });
