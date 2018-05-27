package in.pannu.harpal.notes;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<NoteDB> notesData;
    NotesAdapter notesAdapter;
    DatabaseHelper dbHelper;
    Spinner materialDesignSpinner;
    SharedPreferences filterData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        requestStoragePermission();
        notesData = new ArrayList<>();
        filterData = getSharedPreferences("filterData", Context.MODE_PRIVATE);
        notesData = dbHelper.pullNotes(dbHelper.getReadableDatabase(),"");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(this,notesData);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notesAdapter);

        final List<String> categories = new ArrayList<>();
        categories.add("All Notes");
        categories.add("School");
        categories.add("Important");
        categories.add("Shopping");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);
        materialDesignSpinner = findViewById(R.id.spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);
        materialDesignSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {

                        notesData =   dbHelper.sortNotesByTag(dbHelper.getReadableDatabase(),categories.get(position));
                        notesAdapter.reloadData(notesData);
                        runLayoutAnimation(recyclerView);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        //showToast("Spinner1: unselected");
                    }
                });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), AddNote.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadList();
    }

    private void reloadList() {
        notesData = dbHelper.pullNotes(dbHelper.getReadableDatabase(),"");
        notesAdapter.reloadData(notesData);
        runLayoutAnimation(recyclerView);
        materialDesignSpinner.setSelection(0,true);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        final MenuItem atoZ = menu.findItem(R.id.aToZ);
        final MenuItem lastToFirst = menu.findItem(R.id.lastToFirst);
        final MenuItem ZtoA = menu.findItem(R.id.ZtoA);
        final MenuItem firstToLast = menu.findItem(R.id.firstToLast);

        int filterSet = filterData.getInt("data",1);
        switch (filterSet){
            case 1:
                atoZ.setChecked(true);
                break;
            case 2:
                ZtoA.setChecked(true);
                break;
            case 3:
                firstToLast.setChecked(true);
                break;
            case 4:
                lastToFirst.setChecked(true);
                break;
        }

        atoZ.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override

            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = filterData.edit();
                editor.putInt("data", 1);
                editor.apply();
                reloadList();
                return true;
            }

        });





        ZtoA.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override

            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = filterData.edit();
                editor.putInt("data", 2);
                editor.apply();
                reloadList();
                return true;
            }
        });




        firstToLast.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override

            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = filterData.edit();
                editor.putInt("data", 3);
                editor.apply();
                reloadList();
                return true;
            }

        });



        lastToFirst.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override

            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = filterData.edit();
                editor.putInt("data", 4);
                editor.apply();
                reloadList();
                return true;
            }

        });
        return super.onMenuOpened(featureId, menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        if (manager != null) {
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        }



        MenuItem menuItem = menu.findItem(R.id.search);



        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                materialDesignSpinner.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                materialDesignSpinner.setVisibility(View.VISIBLE);
                materialDesignSpinner.setSelection(0,true);
                return true;
            }
        });



        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                notesData = dbHelper.pullNotes(dbHelper.getReadableDatabase(), s);
                notesAdapter.reloadData(notesData);
                runLayoutAnimation(recyclerView);
                return false;
            }

        });

        return true;


    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (!report.areAllPermissionsGranted()) {
                            showSettingsDialog();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
