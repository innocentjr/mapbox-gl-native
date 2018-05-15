package com.mapbox.mapboxsdk.maps;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;

import com.mapbox.mapboxsdk.R;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.utils.ColorUtils;

/**
 * Settings for the user interface of a MapboxMap. To obtain this interface, call getUiSettings().
 */
public final class UiSettings extends ViewModel {
  // TODO: 15.05.18 cleanup unused constants
  // TODO: 15.05.18 change post values to set values
  private Projection projection; // TODO: 14.05.18 clear projection during maps destroy?

  private AttributionDialogManager attributionDialogManager;

  // compass settings
  private final MutableLiveData<Integer> compassGravity = new MutableLiveData<>();
  private final MutableLiveData<Drawable> compassImage = new MutableLiveData<>();
  private final MutableLiveData<Boolean> compassEnabled = new MutableLiveData<>();
  private final MutableLiveData<Boolean> compassFadeFacingNorth = new MutableLiveData<>();
  private final MutableLiveData<Integer[]> compassMargins = new MutableLiveData<>();
  private final MutableLiveData<Double> compassRotation = new MutableLiveData<>();

  // attribution settings
  private final MutableLiveData<Integer> attributionGravity = new MutableLiveData<>();
  private final MutableLiveData<Boolean> attributionEnabled = new MutableLiveData<>();
  private final MutableLiveData<Integer> attributionTintColor = new MutableLiveData<>();
  private final MutableLiveData<Integer[]> attributionMargins = new MutableLiveData<>();

  // logo view
  private final MutableLiveData<Integer> logoGravity = new MutableLiveData<>();
  private final MutableLiveData<Boolean> logoEnabled = new MutableLiveData<>();
  private final MutableLiveData<Integer[]> logoMargins = new MutableLiveData<>();


  private boolean rotateGesturesEnabled = true;

  private boolean tiltGesturesEnabled = true;

  private boolean zoomGesturesEnabled = true;

  private boolean scrollGesturesEnabled = true;

  private boolean zoomControlsEnabled;

  private boolean doubleTapGesturesEnabled = true;

  private boolean scaleVelocityAnimationEnabled = true;
  private boolean rotateVelocityAnimationEnabled = true;
  private boolean flingVelocityAnimationEnabled = true;

  private boolean increaseRotateThresholdWhenScaling = true;
  private boolean increaseScaleThresholdWhenRotating = true;

  private boolean deselectMarkersOnTap = true;

  private final MutableLiveData<PointF> userProvidedFocalPoint = new MutableLiveData<>();

  void initialiseViews(@NonNull Projection projection) {
    this.projection = projection;
  }

  void initialiseOptions(@NonNull Context context, @NonNull MapboxMapOptions options) {
    Resources resources = context.getResources();
    initialiseGestures(options);
    initialiseCompass(options, resources);
    initialiseLogo(options, resources);
    initialiseAttribution(context, options);
    initialiseZoomControl(context);
  }

  void onSaveInstanceState(Bundle outState) {
    saveGestures(outState);
    saveZoomControl(outState);
    saveDeselectMarkersOnTap(outState);
    saveFocalPoint(outState);
  }

  void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    restoreGestures(savedInstanceState);
    restoreZoomControl(savedInstanceState);
    restoreDeselectMarkersOnTap(savedInstanceState);
    restoreFocalPoint(savedInstanceState);
  }

  private void initialiseGestures(MapboxMapOptions options) {
    setZoomGesturesEnabled(options.getZoomGesturesEnabled());
    setScrollGesturesEnabled(options.getScrollGesturesEnabled());
    setRotateGesturesEnabled(options.getRotateGesturesEnabled());
    setTiltGesturesEnabled(options.getTiltGesturesEnabled());
    setZoomControlsEnabled(options.getZoomControlsEnabled());
    setDoubleTapGesturesEnabled(options.getDoubleTapGesturesEnabled());
  }

  private void saveGestures(Bundle outState) {
    outState.putBoolean(MapboxConstants.STATE_ZOOM_ENABLED, isZoomGesturesEnabled());
    outState.putBoolean(MapboxConstants.STATE_SCROLL_ENABLED, isScrollGesturesEnabled());
    outState.putBoolean(MapboxConstants.STATE_ROTATE_ENABLED, isRotateGesturesEnabled());
    outState.putBoolean(MapboxConstants.STATE_TILT_ENABLED, isTiltGesturesEnabled());
    outState.putBoolean(MapboxConstants.STATE_DOUBLE_TAP_ENABLED, isDoubleTapGesturesEnabled());
    outState.putBoolean(MapboxConstants.STATE_SCALE_ANIMATION_ENABLED, isScaleVelocityAnimationEnabled());
    outState.putBoolean(MapboxConstants.STATE_ROTATE_ANIMATION_ENABLED, isRotateVelocityAnimationEnabled());
    outState.putBoolean(MapboxConstants.STATE_FLING_ANIMATION_ENABLED, isFlingVelocityAnimationEnabled());
    outState.putBoolean(MapboxConstants.STATE_INCREASE_ROTATE_THRESHOLD, isIncreaseRotateThresholdWhenScaling());
    outState.putBoolean(MapboxConstants.STATE_INCREASE_SCALE_THRESHOLD, isIncreaseScaleThresholdWhenRotating());
  }

  private void restoreGestures(Bundle savedInstanceState) {
    setZoomGesturesEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_ZOOM_ENABLED));
    setScrollGesturesEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_SCROLL_ENABLED));
    setRotateGesturesEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_ROTATE_ENABLED));
    setTiltGesturesEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_TILT_ENABLED));
    setDoubleTapGesturesEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_DOUBLE_TAP_ENABLED));
    setScaleVelocityAnimationEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_SCALE_ANIMATION_ENABLED));
    setRotateVelocityAnimationEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_ROTATE_ANIMATION_ENABLED));
    setFlingVelocityAnimationEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_FLING_ANIMATION_ENABLED));
    setIncreaseRotateThresholdWhenScaling(
      savedInstanceState.getBoolean(MapboxConstants.STATE_INCREASE_ROTATE_THRESHOLD));
    setIncreaseScaleThresholdWhenRotating(
      savedInstanceState.getBoolean(MapboxConstants.STATE_INCREASE_SCALE_THRESHOLD));
  }

  private void initialiseCompass(MapboxMapOptions options, Resources resources) {
    setCompassEnabled(options.getCompassEnabled());
    setCompassGravity(options.getCompassGravity());
    int[] compassMargins = options.getCompassMargins();
    if (compassMargins != null) {
      setCompassMargins(compassMargins[0], compassMargins[1], compassMargins[2], compassMargins[3]);
    } else {
      int tenDp = (int) resources.getDimension(R.dimen.mapbox_four_dp);
      setCompassMargins(tenDp, tenDp, tenDp, tenDp);
    }
    setCompassFadeFacingNorth(options.getCompassFadeFacingNorth());
    if (options.getCompassImage() == null) {
      options.compassImage(ResourcesCompat.getDrawable(resources, R.drawable.mapbox_compass_icon, null));
    }
    setCompassImage(options.getCompassImage());
  }

  private void initialiseLogo(MapboxMapOptions options, Resources resources) {
    setLogoEnabled(options.getLogoEnabled());
    setLogoGravity(options.getLogoGravity());
    setLogoMargins(resources, options.getLogoMargins());
  }

  private void setLogoMargins(Resources resources, int[] logoMargins) {
    if (logoMargins != null) {
      setLogoMargins(logoMargins[0], logoMargins[1], logoMargins[2], logoMargins[3]);
    } else {
      // user did not specify margins when programmatically creating a map
      int fourDp = (int) resources.getDimension(R.dimen.mapbox_four_dp);
      setLogoMargins(fourDp, fourDp, fourDp, fourDp);
    }
  }

  private void initialiseAttribution(Context context, MapboxMapOptions options) {
    setAttributionEnabled(options.getAttributionEnabled());
    setAttributionGravity(options.getAttributionGravity());
    setAttributionMargins(context, options.getAttributionMargins());
    int attributionTintColor = options.getAttributionTintColor();
    setAttributionTintColor(attributionTintColor != -1
      ? attributionTintColor : ColorUtils.getPrimaryColor(context));
  }

  private void setAttributionMargins(Context context, int[] attributionMargins) {
    if (attributionMargins != null) {
      setAttributionMargins(attributionMargins[0], attributionMargins[1],
        attributionMargins[2], attributionMargins[3]);
    } else {
      // user did not specify margins when programmatically creating a map
      Resources resources = context.getResources();
      int margin = (int) resources.getDimension(R.dimen.mapbox_four_dp);
      int leftMargin = (int) resources.getDimension(R.dimen.mapbox_ninety_two_dp);
      setAttributionMargins(leftMargin, margin, margin, margin);
    }
  }

  private void initialiseZoomControl(Context context) {
    if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
      setZoomControlsEnabled(true);
    }
  }

  private void saveZoomControl(Bundle outState) {
    outState.putBoolean(MapboxConstants.STATE_ZOOM_CONTROLS_ENABLED, isZoomControlsEnabled());
  }

  private void restoreZoomControl(Bundle savedInstanceState) {
    setZoomControlsEnabled(savedInstanceState.getBoolean(MapboxConstants.STATE_ZOOM_CONTROLS_ENABLED));
  }

  /**
   * <p>
   * Enables or disables the compass. The compass is an icon on the map that indicates the
   * direction of north on the map. When a user clicks
   * the compass, the camera orients itself to its default orientation and fades away shortly
   * after. If disabled, the compass will never be displayed.
   * </p>
   * By default, the compass is enabled.
   *
   * @param compassEnabled True to enable the compass; false to disable the compass.
   */
  public void setCompassEnabled(boolean enabled) {
    compassEnabled.postValue(enabled);
  }

  /**
   * Returns whether the compass is enabled.
   *
   * @return True if the compass is enabled; false if the compass is disabled.
   */
  public boolean isCompassEnabled() {
    return compassEnabled.getValue();
  }

  /**
   * Returns whether the compass is enabled observable.
   *
   * @return Compass enabled observable.
   */
  public MutableLiveData<Boolean> isCompassEnabledObservable() {
    return compassEnabled;
  }

  /**
   * <p>
   * Sets the gravity of the compass view. Use this to change the corner of the map view that the
   * compass is displayed in.
   * </p>
   * By default, the compass is in the top right corner.
   *
   * @param gravity Android SDK Gravity.
   */
  @UiThread
  public void setCompassGravity(int gravity) {
    compassGravity.postValue(gravity);
  }

  /**
   * Enables or disables fading of the compass when facing north.
   * <p>
   * By default this feature is enabled
   * </p>
   *
   * @param compassFadeFacingNorth True to enable the fading animation; false to disable it
   */
  public void setCompassFadeFacingNorth(boolean fadeFacingNorth) {
    compassFadeFacingNorth.postValue(fadeFacingNorth);
  }

  /**
   * Specifies the CompassView image.
   * <p>
   * By default this value is R.drawable.mapbox_compass_icon.
   * </p>
   *
   * @param compass the drawable to show as image compass
   */
  public void setCompassImage(Drawable compass) {
    compassImage.postValue(compass);
  }

  /**
   * Returns whether the compass performs a fading animation out when facing north.
   *
   * @return True if the compass will fade, false if it remains visible
   */
  public boolean isCompassFadeWhenFacingNorth() {
    return compassFadeFacingNorth.getValue();
  }

  /**
   * Returns whether the compass performs a fading animation out when facing north observable.
   *
   * @return Compass fading north observable.
   */
  public MutableLiveData<Boolean> isCompassFadeWhenFacingNorthObservable() {
    return compassFadeFacingNorth;
  }

  /**
   * Returns the gravity value of the CompassView
   *
   * @return The gravity
   */
  public int getCompassGravity() {
    return compassGravity.getValue();
  }

  /**
   * Returns the compass gravity value observable
   *
   * @return The gravity observable
   */
  public MutableLiveData<Integer> getCompassGravityObservable() {
    return compassGravity;
  }

  /**
   * Sets the margins of the compass view. Use this to change the distance of the compass from the
   * map view edge.
   *
   * @param left   The left margin in pixels.
   * @param top    The top margin in pixels.
   * @param right  The right margin in pixels.
   * @param bottom The bottom margin in pixels.
   */
  @UiThread
  public void setCompassMargins(int left, int top, int right, int bottom) {
    compassMargins.postValue(new Integer[] {left, top, right, bottom});
  }

  /**
   * Returns the left side margin of CompassView
   *
   * @return The left margin in pixels
   */
  public int getCompassMarginLeft() {
    return compassMargins.getValue()[0];
  }

  /**
   * Returns the top side margin of CompassView
   *
   * @return The top margin in pixels
   */
  public int getCompassMarginTop() {
    return compassMargins.getValue()[1];
  }

  /**
   * Returns the right side margin of CompassView
   *
   * @return The right margin in pixels
   */
  public int getCompassMarginRight() {
    return compassMargins.getValue()[2];
  }

  /**
   * Returns the bottom side margin of CompassView
   *
   * @return The bottom margin in pixels
   */
  public int getCompassMarginBottom() {
    return compassMargins.getValue()[3];
  }

  /**
   * Get Compass View margins observable.
   *
   * @return Compass View margins observable.
   */
  public MutableLiveData<Integer[]> getCompassMarginsObservable() {
    return compassMargins;
  }

  /**
   * Get the current configured CompassView image.
   *
   * @return the drawable used as compass image
   */
  public Drawable getCompassImage() {
    return compassImage.getValue();
  }

  /**
   * Get the CompassView image observable.
   *
   * @return the observable of the drawable used as compass image
   */
  public MutableLiveData<Drawable> getCompassImageObservable() {
    return compassImage;
  }

  void updateCompass(@NonNull CameraPosition cameraPosition) {
    double clockwiseBearing = -cameraPosition.bearing;
    compassRotation.postValue(clockwiseBearing);
  }

  public MutableLiveData<Double> getCompassRotationObservable() {
    return compassRotation;
  }

  /**
   * <p>
   * Enables or disables the Mapbox logo.
   * </p>
   * By default, the logo is enabled.
   *
   * @param enabled True to enable the logo; false to disable the logo.
   */
  public void setLogoEnabled(boolean enabled) {
    logoEnabled.postValue(enabled);
  }

  /**
   * Returns whether the logo is enabled.
   *
   * @return True if the logo is enabled; false if the logo is disabled.
   */
  public boolean isLogoEnabled() {
    return logoEnabled.getValue();
  }

  /**
   * Returns whether the logo is enabled observable.
   *
   * @return Logo is enabled observable.
   */
  public MutableLiveData<Boolean> isLogoEnabledObservable() {
    return logoEnabled;
  }

  /**
   * <p>
   * Sets the gravity of the logo view. Use this to change the corner of the map view that the
   * Mapbox logo is displayed in.
   * </p>
   * By default, the logo is in the bottom left corner.
   *
   * @param gravity Android SDK Gravity.
   */
  public void setLogoGravity(int gravity) {
    logoGravity.postValue(gravity);
  }

  /**
   * Returns the gravity value of the logo
   *
   * @return The gravity
   */
  public int getLogoGravity() {
    return logoGravity.getValue();
  }

  /**
   * Returns the observable gravity value of the logo
   *
   * @return The observable gravity
   */
  public MutableLiveData<Integer> getLogoGravityObservable() {
    return logoGravity;
  }

  /**
   * Sets the margins of the logo view. Use this to change the distance of the Mapbox logo from the
   * map view edge.
   *
   * @param left   The left margin in pixels.
   * @param top    The top margin in pixels.
   * @param right  The right margin in pixels.
   * @param bottom The bottom margin in pixels.
   */
  public void setLogoMargins(int left, int top, int right, int bottom) {
    logoMargins.postValue(new Integer[] {left, top, right, bottom});
  }

  /**
   * Returns the left side margin of the logo
   *
   * @return The left margin in pixels
   */
  public int getLogoMarginLeft() {
    return logoMargins.getValue()[0];
  }

  /**
   * Returns the top side margin of the logo
   *
   * @return The top margin in pixels
   */
  public int getLogoMarginTop() {
    return logoMargins.getValue()[1];
  }

  /**
   * Returns the right side margin of the logo
   *
   * @return The right margin in pixels
   */
  public int getLogoMarginRight() {
    return logoMargins.getValue()[2];
  }

  /**
   * Returns the bottom side margin of the logo
   *
   * @return The bottom margin in pixels
   */
  public int getLogoMarginBottom() {
    return logoMargins.getValue()[3];
  }

  /**
   * Returns Logo view margins observable.
   *
   * @return Logo view margins observable.
   */
  public MutableLiveData<Integer[]> getLogoMarginsObservable() {
    return logoMargins;
  }

  /**
   * <p>
   * Enables or disables the attribution.
   * </p>
   * By default, the attribution is enabled.
   *
   * @param enabled True to enable the attribution; false to disable the attribution.
   */
  public void setAttributionEnabled(boolean enabled) {
    attributionEnabled.postValue(enabled);
  }

  /**
   * Returns whether the attribution is enabled.
   *
   * @return True if the attribution is enabled; false if the attribution is disabled.
   */
  public boolean isAttributionEnabled() {
    return attributionEnabled.getValue();
  }

  /**
   * Returns whether the attribution is enabled observable.
   *
   * @return Attribution is enabled observable.
   */
  public MutableLiveData<Boolean> isAttributionEnabledObservable() {
    return attributionEnabled;
  }

  /**
   * Set a custom attribution dialog manager.
   * <p>
   * Set to null to reset to default behaviour.
   * </p>
   *
   * @param attributionDialogManager the manager class used for showing attribution
   */
  public void setAttributionDialogManager(AttributionDialogManager attributionDialogManager) {
    this.attributionDialogManager = attributionDialogManager;
  }

  /**
   * Get the custom attribution dialog manager.
   *
   * @return the active manager class used for showing attribution
   */
  public AttributionDialogManager getAttributionDialogManager() {
    return attributionDialogManager;
  }

  /**
   * <p>
   * Sets the gravity of the attribution.
   * </p>
   * By default, the attribution is in the bottom left corner next to the Mapbox logo.
   *
   * @param gravity Android SDK Gravity.
   */
  public void setAttributionGravity(int gravity) {
    attributionGravity.postValue(gravity);
  }

  /**
   * Returns the gravity value of the logo
   *
   * @return The gravity
   */
  public int getAttributionGravity() {
    return attributionGravity.getValue();
  }

  /**
   * Returns the gravity value of the logo observable.
   *
   * @return The gravity observable.
   */
  public MutableLiveData<Integer> getAttributionGravityObservable() {
    return attributionGravity;
  }

  /**
   * Sets the margins of the attribution view.
   *
   * @param left   The left margin in pixels.
   * @param top    The top margin in pixels.
   * @param right  The right margin in pixels.
   * @param bottom The bottom margin in pixels.
   */
  public void setAttributionMargins(int left, int top, int right, int bottom) {
    attributionMargins.postValue(new Integer[] {left, top, right, bottom});
  }

  /**
   * <p>
   * Sets the tint of the attribution view. Use this to change the color of the attribution.
   * </p>
   * By default, the logo is tinted with the primary color of your theme.
   *
   * @param tintColor Color to tint the attribution.
   */
  public void setAttributionTintColor(@ColorInt int tintColor) {
    attributionTintColor.postValue(tintColor);
  }

  /**
   * Get Attribution tint color observable.
   *
   * @return Attribution tint color observable.
   */
  public MutableLiveData<Integer> getAttributionTintColorObservable() {
    return attributionTintColor;
  }

  /**
   * Returns the left side margin of the attribution view.
   *
   * @return The left margin in pixels
   */
  public int getAttributionMarginLeft() {
    return attributionMargins.getValue()[0];
  }

  /**
   * Returns the top side margin of the attribution view.
   *
   * @return The top margin in pixels
   */
  public int getAttributionMarginTop() {
    return attributionMargins.getValue()[1];
  }

  /**
   * Returns the right side margin of the attribution view.
   *
   * @return The right margin in pixels
   */
  public int getAttributionMarginRight() {
    return attributionMargins.getValue()[2];
  }

  /**
   * Returns the bottom side margin of the logo
   *
   * @return The bottom margin in pixels
   */
  public int getAttributionMarginBottom() {
    return attributionMargins.getValue()[3];
  }

  /**
   * Returns Attribution margins observable.
   *
   * @return Attribution margins observable.
   */
  public MutableLiveData<Integer[]> getAttributionMarginsObservable() {
    return attributionMargins;
  }

  /**
   * <p>
   * Changes whether the user may rotate the map.
   * </p>
   * <p>
   * This setting controls only user interactions with the map. If you set the value to false,
   * you may still change the map location programmatically.
   * </p>
   * The default value is true.
   *
   * @param rotateGesturesEnabled If true, rotating is enabled.
   */
  public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
    this.rotateGesturesEnabled = rotateGesturesEnabled;
  }

  /**
   * Returns whether the user may rotate the map.
   *
   * @return If true, rotating is enabled.
   */
  public boolean isRotateGesturesEnabled() {
    return rotateGesturesEnabled;
  }

  /**
   * <p>
   * Changes whether the user may tilt the map.
   * </p>
   * <p>
   * This setting controls only user interactions with the map. If you set the value to false,
   * you may still change the map location programmatically.
   * </p>
   * The default value is true.
   *
   * @param tiltGesturesEnabled If true, tilting is enabled.
   */
  public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
    this.tiltGesturesEnabled = tiltGesturesEnabled;

  }

  /**
   * Returns whether the user may tilt the map.
   *
   * @return If true, tilting is enabled.
   */
  public boolean isTiltGesturesEnabled() {
    return tiltGesturesEnabled;
  }

  /**
   * <p>
   * Changes whether the user may zoom the map.
   * </p>
   * <p>
   * This setting controls only user interactions with the map. If you set the value to false,
   * you may still change the map location programmatically.
   * </p>
   * The default value is true.
   *
   * @param zoomGesturesEnabled If true, zooming is enabled.
   */
  public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
    this.zoomGesturesEnabled = zoomGesturesEnabled;
  }

  /**
   * Returns whether the user may zoom the map.
   *
   * @return If true, zooming is enabled.
   */
  public boolean isZoomGesturesEnabled() {
    return zoomGesturesEnabled;
  }

  /**
   * <p>
   * Sets whether the zoom controls are enabled.
   * If enabled, the zoom controls are a pair of buttons
   * (one for zooming in, one for zooming out) that appear on the screen.
   * When pressed, they cause the camera to zoom in (or out) by one zoom level.
   * If disabled, the zoom controls are not shown.
   * </p>
   * By default the zoom controls are enabled if the device is only single touch capable;
   *
   * @param zoomControlsEnabled If true, the zoom controls are enabled.
   */
  public void setZoomControlsEnabled(boolean zoomControlsEnabled) {
    this.zoomControlsEnabled = zoomControlsEnabled;
  }

  /**
   * Gets whether the zoom controls are enabled.
   *
   * @return If true, the zoom controls are enabled.
   */
  public boolean isZoomControlsEnabled() {
    return zoomControlsEnabled;
  }

  /**
   * <p>
   * Changes whether the user may zoom the map with a double tap.
   * </p>
   * <p>
   * This setting controls only user interactions with the map. If you set the value to false,
   * you may still change the map location programmatically.
   * </p>
   * The default value is true.
   *
   * @param doubleTapGesturesEnabled If true, zooming with a double tap is enabled.
   */
  public void setDoubleTapGesturesEnabled(boolean doubleTapGesturesEnabled) {
    this.doubleTapGesturesEnabled = doubleTapGesturesEnabled;
  }

  /**
   * Returns whether the user may zoom the map with a double tap.
   *
   * @return If true, zooming with a double tap is enabled.
   */
  public boolean isDoubleTapGesturesEnabled() {
    return doubleTapGesturesEnabled;
  }

  private void restoreDeselectMarkersOnTap(Bundle savedInstanceState) {
    setDeselectMarkersOnTap(savedInstanceState.getBoolean(MapboxConstants.STATE_DESELECT_MARKER_ON_TAP));
  }

  private void saveDeselectMarkersOnTap(Bundle outState) {
    outState.putBoolean(MapboxConstants.STATE_DESELECT_MARKER_ON_TAP, isDeselectMarkersOnTap());
  }

  /**
   * Gets whether the markers are automatically deselected (and therefore, their infowindows
   * closed) when a map tap is detected.
   *
   * @return If true, markers are deselected on a map tap.
   */
  public boolean isDeselectMarkersOnTap() {
    return deselectMarkersOnTap;
  }

  /**
   * Sets whether the markers are automatically deselected (and therefore, their infowindows
   * closed) when a map tap is detected.
   *
   * @param deselectMarkersOnTap determines if markers should be deslected on tap
   */
  public void setDeselectMarkersOnTap(boolean deselectMarkersOnTap) {
    this.deselectMarkersOnTap = deselectMarkersOnTap;
  }

  /**
   * <p>
   * Changes whether the user may scroll around the map.
   * </p>
   * <p>
   * This setting controls only user interactions with the map. If you set the value to false,
   * you may still change the map location programmatically.
   * </p>
   * The default value is true.
   *
   * @param scrollGesturesEnabled If true, scrolling is enabled.
   */
  public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
    this.scrollGesturesEnabled = scrollGesturesEnabled;
  }

  /**
   * Returns whether the user may scroll around the map.
   *
   * @return If true, scrolling is enabled.
   */
  public boolean isScrollGesturesEnabled() {
    return scrollGesturesEnabled;
  }

  /**
   * Returns whether scale velocity animation should execute after users finishes a gesture.
   *
   * @return If true, scale velocity animation is enabled.
   */
  public boolean isScaleVelocityAnimationEnabled() {
    return scaleVelocityAnimationEnabled;
  }

  /**
   * Set whether scale velocity animation should execute after users finishes a gesture. True by default.
   *
   * @param scaleVelocityAnimationEnabled If true, scale velocity animation will be enabled.
   */
  public void setScaleVelocityAnimationEnabled(boolean scaleVelocityAnimationEnabled) {
    this.scaleVelocityAnimationEnabled = scaleVelocityAnimationEnabled;
  }

  /**
   * Returns whether rotate velocity animation should execute after users finishes a gesture.
   *
   * @return If true, rotate velocity animation is enabled.
   */
  public boolean isRotateVelocityAnimationEnabled() {
    return rotateVelocityAnimationEnabled;
  }

  /**
   * Set whether rotate velocity animation should execute after users finishes a gesture. True by default.
   *
   * @param rotateVelocityAnimationEnabled If true, rotate velocity animation will be enabled.
   */
  public void setRotateVelocityAnimationEnabled(boolean rotateVelocityAnimationEnabled) {
    this.rotateVelocityAnimationEnabled = rotateVelocityAnimationEnabled;
  }

  /**
   * Returns whether fling velocity animation should execute after users finishes a gesture.
   *
   * @return If true, fling velocity animation is enabled.
   */
  public boolean isFlingVelocityAnimationEnabled() {
    return flingVelocityAnimationEnabled;
  }

  /**
   * Set whether fling velocity animation should execute after users finishes a gesture. True by default.
   *
   * @param flingVelocityAnimationEnabled If true, fling velocity animation will be enabled.
   */
  public void setFlingVelocityAnimationEnabled(boolean flingVelocityAnimationEnabled) {
    this.flingVelocityAnimationEnabled = flingVelocityAnimationEnabled;
  }

  /**
   * Set whether all velocity animations should execute after users finishes a gesture.
   *
   * @param allVelocityAnimationsEnabled If true, all velocity animations will be enabled.
   */
  public void setAllVelocityAnimationsEnabled(boolean allVelocityAnimationsEnabled) {
    setScaleVelocityAnimationEnabled(allVelocityAnimationsEnabled);
    setRotateVelocityAnimationEnabled(allVelocityAnimationsEnabled);
    setFlingVelocityAnimationEnabled(allVelocityAnimationsEnabled);
  }

  /**
   * Returns whether rotation threshold should be increase whenever scale is detected.
   *
   * @return If true, rotation threshold will be increased.
   */
  public boolean isIncreaseRotateThresholdWhenScaling() {
    return increaseRotateThresholdWhenScaling;
  }

  /**
   * Set whether rotation threshold should be increase whenever scale is detected.
   *
   * @param increaseRotateThresholdWhenScaling If true, rotation threshold will be increased.
   */
  public void setIncreaseRotateThresholdWhenScaling(boolean increaseRotateThresholdWhenScaling) {
    this.increaseRotateThresholdWhenScaling = increaseRotateThresholdWhenScaling;
  }

  /**
   * Returns whether scale threshold should be increase whenever rotation is detected.
   *
   * @return If true, scale threshold will be increased.
   */
  public boolean isIncreaseScaleThresholdWhenRotating() {
    return increaseScaleThresholdWhenRotating;
  }

  /**
   * set whether scale threshold should be increase whenever rotation is detected.
   *
   * @param increaseScaleThresholdWhenRotating If true, scale threshold will be increased.
   */
  public void setIncreaseScaleThresholdWhenRotating(boolean increaseScaleThresholdWhenRotating) {
    this.increaseScaleThresholdWhenRotating = increaseScaleThresholdWhenRotating;
  }

  /**
   * <p>
   * Sets the preference for whether all gestures should be enabled or disabled.
   * </p>
   * <p>
   * This setting controls only user interactions with the map. If you set the value to false,
   * you may still change the map location programmatically.
   * </p>
   * The default value is true.
   *
   * @param enabled If true, all gestures are available; otherwise, all gestures are disabled.
   * @see #setZoomGesturesEnabled(boolean) )
   * @see #setScrollGesturesEnabled(boolean)
   * @see #setRotateGesturesEnabled(boolean)
   * @see #setTiltGesturesEnabled(boolean)
   */
  public void setAllGesturesEnabled(boolean enabled) {
    setScrollGesturesEnabled(enabled);
    setRotateGesturesEnabled(enabled);
    setTiltGesturesEnabled(enabled);
    setZoomGesturesEnabled(enabled);
    setDoubleTapGesturesEnabled(enabled);
  }

  private void saveFocalPoint(Bundle outState) {
    outState.putParcelable(MapboxConstants.STATE_USER_FOCAL_POINT, getFocalPoint());
  }

  private void restoreFocalPoint(Bundle savedInstanceState) {
    PointF pointF = savedInstanceState.getParcelable(MapboxConstants.STATE_USER_FOCAL_POINT);
    if (pointF != null) {
      setFocalPoint(pointF);
    }
  }

  /**
   * Sets the focal point used as center for a gesture
   *
   * @param focalPoint the focal point to be used.
   */
  public void setFocalPoint(@Nullable PointF focalPoint) {
    userProvidedFocalPoint.postValue(focalPoint);
  }

  /**
   * Returns the gesture focal point
   *
   * @return The focal point
   */
  public PointF getFocalPoint() {
    return userProvidedFocalPoint.getValue();
  }

  /**
   * Returns {@link MutableLiveData} object of the focal point.
   *
   * @return observable focal point object
   */
  public MutableLiveData<PointF> getFocalPointObservable() {
    return userProvidedFocalPoint;
  }

  /**
   * Returns the measured height of the MapView
   *
   * @return height in pixels
   */
  public float getHeight() {
    return projection.getHeight();
  }

  /**
   * Returns the measured width of the MapView
   *
   * @return widht in pixels
   */
  public float getWidth() {
    return projection.getWidth();
  }

  /**
   * Invalidates the ViewSettings instances shown on top of the MapView
   */
  public void invalidate() {
    setLogoMargins(getLogoMarginLeft(), getLogoMarginTop(), getLogoMarginRight(), getLogoMarginBottom());
    setCompassMargins(getCompassMarginLeft(), getCompassMarginTop(), getCompassMarginRight(), getCompassMarginBottom());
    setAttributionMargins(getAttributionMarginLeft(), getAttributionMarginTop(), getAttributionMarginRight(),
      getAttributionMarginBottom());
  }
}
