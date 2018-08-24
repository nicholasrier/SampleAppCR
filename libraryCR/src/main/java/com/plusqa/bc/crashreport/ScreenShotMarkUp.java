package com.plusqa.bc.crashreport;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;


public class ScreenShotMarkUp extends AppCompatActivity {

    private AnnotationView screenshotView;

    FloatingActionButton fabCurrentTool, fabTool1, fabTool2, fabShapeOption, fabTool3, fabDelete;
    FloatingActionButton colorFAB1, colorFAB2, colorFAB3;
    LinearLayout fabLayoutDraw, fabLayoutShapes, fabLayoutShapeOption,
            fabLayoutText, fabCurrentToolLayout, fabLayoutDelete, screenshotLayout;
    LinearLayout fabLayoutRed, fabLayoutGreen, fabLayoutBlack;
    View fabBGLayout;
    boolean isFABOpen = false;

    private int drawIconID;
    private int squareIconID;
    private int textIconID;
    private int circlesIconID;
    private int shapeIconID;
    private int currentIconID;

    public int currentColor;
    private int selectedColor;
    private int previousColor;
    private int fab1Color;
    private int fab2Color;
    private int fab3Color;

    boolean isShapeOptionVisible = false;

    private static final int green = Color.parseColor("#51ccc0");
    private static final int red = Color.parseColor("#FF5252");
    private static final int black = Color.parseColor("#000000");
    private static final int blue = Color.parseColor("#519ACC");

    private InputMethodManager imm;

    private int toolIconID1;
    private int toolIconID2;
    private int toolIconID3;

    View activityRootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot_mark_up);

        //Set up toolbar
        Toolbar mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        //load screenshot into screenShotImageView
        screenshotView = new AnnotationView(ScreenShotMarkUp.this);

        screenshotView.setId(View.generateViewId());
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

        screenshotView.setLayoutParams(lp);
        screenshotView.setBackgroundColor(ContextCompat.getColor(ScreenShotMarkUp.this, R.color.colorTransBgCR)
        );
        screenshotView.setFitsSystemWindows(false);

        //adding view to layout
        screenshotLayout = findViewById(R.id.screenshotLayout);
        screenshotLayout.addView(screenshotView);


        Bitmap screenShotBM = Utils.getBitMap(getApplicationContext(), CrashReport.image_name);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), screenShotBM);

        screenshotView.setBackground(bitmapDrawable);
        screenshotView.setToolFlag(AnnotationView.DRAW_TOOL);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (screenShotBM.getWidth() > dm.widthPixels) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (screenShotBM.getHeight() > dm.widthPixels) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        drawIconID = R.drawable.ic_brush_black_24dp;
        squareIconID = R.drawable.ic_sharp_crop_square_white_24px;
        shapeIconID = squareIconID;
        circlesIconID = R.drawable.ic_sharp_bubble_chart_24px;
        textIconID = R.drawable.ic_text_fields_white_24dp;
        currentIconID = drawIconID;
        toolIconID1 = circlesIconID;
        toolIconID2 = squareIconID;
        toolIconID3 = textIconID;

        //FAB tool menu layouts
        fabLayoutDraw = findViewById(R.id.fabLayout1);
        fabLayoutShapes = findViewById(R.id.fabLayout2);
        fabLayoutShapeOption = findViewById(R.id.fabShapesLayout_option);
        fabLayoutText = findViewById(R.id.fabLayout3);
        fabCurrentToolLayout = findViewById(R.id.fabCurrentToolLayout);
        fabLayoutDelete = findViewById(R.id.fabDeleteLayout);

        //FAB color menu layouts
        fabLayoutRed = findViewById(R.id.fabColorLayout_red);
        fabLayoutGreen = findViewById(R.id.fabColorLayout_green);
        fabLayoutBlack = findViewById(R.id.fabColorLayout_black);
        //tool FABs
        fabCurrentTool = findViewById(R.id.fab);
        fabTool1 = findViewById(R.id.fab1);
        fabTool2 = findViewById(R.id.fab2);
        fabShapeOption = findViewById(R.id.fab_shapeOption);
        fabTool3 = findViewById(R.id.fab3);
        fabDelete = findViewById(R.id.fabDelete);
        //color FABs
        colorFAB1 = findViewById(R.id.fab_red);
        colorFAB2 = findViewById(R.id.fab_green);
        colorFAB3 = findViewById(R.id.fab_black);

        currentColor = green;
        selectedColor = green;
        previousColor = green;
        fab1Color = red;
        fab2Color = blue;
        fab3Color = black;

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        fabBGLayout = findViewById(R.id.fabBGLayout);

        activityRootView = findViewById(R.id.coordinatorLayout01);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    boolean doOnce = true;
            @Override
            public void onGlobalLayout() {

                if (doOnce) {
                    fabDelete.hide();
                    doOnce = false;

                    screenshotView.setDeleteArea(new RectF(Utils.getViewRect(fabLayoutDelete)) );
                }

            }
        });

        // Listen for shape hovering over delete tool to animate trash can FAB
        screenshotView.setDeletionListener(new AnnotationView.DeletionListener() {

            @Override
            public void onDeleteFlagChange(Boolean deleteFlag) {

                if (deleteFlag) {

                    fabDelete.setScaleX(1.3f);
                    fabDelete.setScaleY(1.3f);

                } else {

                    fabDelete.setScaleX(1);
                    fabDelete.setScaleY(1);

                }
            }
        });

        // Listen to when user is editing screenshot, so that UI can be hidden/shown appropriately
        screenshotView.setOnAnnotationListener(new AnnotationView.OnAnnotationListener() {
            @Override
            public void onAnnotation(int toolFlag, boolean annotating, boolean isNewDrawing) {
                if (annotating) {

                    fabCurrentTool.hide();

                    if (toolFlag == AnnotationView.DRAW_TOOL && isNewDrawing) {

                        fabDelete.hide();
                    } else {

                        fabDelete.show();
                    }

                } else {

                    fabCurrentTool.show();
                    fabDelete.hide();
                }
            }
        });

        fabCurrentTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFABOpen) {
                    if (!isShapeOptionVisible) {
                        closeFABMenu();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.screenshot_menu, menu);
        return true;
    }

    // Done button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_undo) {

                screenshotView.undo();

                return true;


        } else if (id == R.id.action_redo) {

                screenshotView.redo();

                return true;


        } else if (id == R.id.action_done) {

                screenshotView.setDrawingCacheEnabled(true);
                Bitmap b = screenshotView.getDrawingCache();

                Utils.saveBitmap(ScreenShotMarkUp.this, "edited_image", b);

                Intent intent = new Intent(ScreenShotMarkUp.this, FormatAndSend.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFABMenu() {
        isFABOpen = true;

        fabCurrentTool.animate().rotationBy(180 - fabCurrentTool.getRotation());
        fabLayoutDraw.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayoutShapes.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayoutText.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayoutRed.animate().translationX(-120);
        fabLayoutGreen.animate().translationX(-240);
        fabLayoutBlack.animate().translationX(-360).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                fabBGLayout.setVisibility(View.VISIBLE);
                fabLayoutDraw.setVisibility(View.VISIBLE);
                fabLayoutShapes.setVisibility(View.VISIBLE);
                fabLayoutText.setVisibility(View.VISIBLE);
                fabLayoutRed.setVisibility(View.VISIBLE);
                fabLayoutGreen.setVisibility(View.VISIBLE);
                fabLayoutBlack.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fabShapeOption.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void closeFABMenu() {
        isFABOpen = false;

        fabLayoutRed.animate().translationX(0);
        fabLayoutGreen.animate().translationX(0);
        fabLayoutBlack.animate().translationX(0);
        fabCurrentTool.animate().rotationBy(fabCurrentTool.getRotation() * -1);
        fabLayoutDraw.animate().translationY(0);
        fabLayoutShapes.animate().translationY(0);
        fabLayoutText.animate().translationY(0).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                fabShapeOption.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayoutDraw.setVisibility(View.GONE);
                    fabLayoutShapes.setVisibility(View.GONE);
                    fabLayoutText.setVisibility(View.GONE);
                    fabLayoutRed.setVisibility(View.GONE);
                    fabLayoutGreen.setVisibility(View.GONE);
                    fabLayoutBlack.setVisibility(View.GONE);
                    fabBGLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void tapTool(View v) {
        if (isFABOpen) {
            int iconID;

            if (v == fabTool1) {
                iconID = toolIconID1;
                fabTool1.setImageResource(currentIconID);
                toolIconID1 = currentIconID;
            } else if (v == fabTool2) {
                iconID = toolIconID2;
                fabTool2.setImageResource(currentIconID);
                toolIconID2 = currentIconID;
            } else {
                iconID = toolIconID3;
                fabTool3.setImageResource(currentIconID);
                toolIconID3 = currentIconID;
            }

            currentIconID = iconID;

            boolean isDrawSelected = currentIconID == drawIconID;
            boolean isTextSelected = currentIconID == textIconID;

            boolean isShapesSelected = (currentIconID == squareIconID) || (currentIconID == circlesIconID);

            if (isShapesSelected)
                shapeIconID = (currentIconID == squareIconID) ? squareIconID : circlesIconID;

            closeFABMenu();

            fabCurrentTool.setImageResource(currentIconID);

            if (isDrawSelected) {
                screenshotView.setToolFlag(AnnotationView.DRAW_TOOL);
            } else if (isShapesSelected) {
                if (shapeIconID == squareIconID) {
                    screenshotView.setToolFlag(AnnotationView.RECT_TOOL);
                } else {
                    screenshotView.setToolFlag(AnnotationView.OVAL_TOOL);
                }
            } else {
                screenshotView.setToolFlag(AnnotationView.TEXT_TOOL);
            }
        }
    }


    public void tapColor1(View view) {
        //set color of FABs
        previousColor = currentColor;
        selectedColor = fab1Color;

        selectColor(view);
        fab1Color = previousColor;
        //set color of actual tool
        screenshotView.setPaint(selectedColor);
    }

    public void tapColor2(View view) {
        //set color of FABs
        previousColor = currentColor;
        selectedColor = fab2Color;

        selectColor(view);
        fab2Color = previousColor;
        //set color of actual tool
        screenshotView.setPaint(selectedColor);
    }

    public void tapColor3(View view) {
        //set color of FABs
        previousColor = currentColor;
        selectedColor = fab3Color;

        selectColor(view);
        fab3Color = previousColor;
        //set color of actual tool
        screenshotView.setPaint(selectedColor);
    }

    public void selectColor(final View view) {
        //implement later -- use four colors - don't display active color in color options

        ObjectAnimator animator = ObjectAnimator.ofInt(fabCurrentTool,
                "backgroundTint",
                currentColor,
                selectedColor);
        animator.setDuration(2000L);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int[][] states = new int[][]{
                        new int[]{android.R.attr.state_enabled}, // enabled
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_pressed}  // pressed
                };

                int[] colors = new int[]{
                        red,
                        green,
                        black,
                        blue
                };

                ColorStateList myList = new ColorStateList(states, colors);
                int animatedValue = (int) animation.getAnimatedValue();
                fabCurrentTool.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                fabTool1.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                fabTool2.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                fabShapeOption.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                fabTool3.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                if (selectedColor == fab1Color)
                    colorFAB1.setBackgroundTintList(ColorStateList.valueOf(previousColor));
                else if (selectedColor == fab2Color) {
                    colorFAB2.setBackgroundTintList(ColorStateList.valueOf(previousColor));
                } else if (selectedColor == fab3Color) {
                    colorFAB3.setBackgroundTintList(ColorStateList.valueOf(previousColor));
                }
            }
        });
        animator.start();

        currentColor = selectedColor;
    }

}




