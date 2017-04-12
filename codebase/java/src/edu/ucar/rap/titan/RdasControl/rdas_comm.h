typedef struct {
  int32 cookie;
  int32 radar_id;
  int32 year;
  int32 month;
  int32 day;
  int32 hour;
  int32 min;
  int32 sec;
  int32 msec;
  int32 ngates;
  int32 nfields;
  int32 samples_per_beam;
  int32 polarization_code;
  int32 beam_count;
  int32 tilt_count;
  int32 end_of_tilt_flag;
  int32 end_of_vol_flag;
  int32 flag_status1;
  int32 flag_status2;
  int32 field_codes[12];
  int32 spare_ints[7];
  fl32 az;
  fl32 el;
  fl32 el_target;
  fl32 alt_km;
  fl32 lat_deg;
  fl32 lat_frac_deg;
  fl32 lon_deg;
  fl32 lon_frac_deg;
  fl32 gate_spacing;
  fl32 start_range;
  fl32 pulse_width;
  fl32 prf;
  fl32 analog_status[8];
  fl32 spare_floats[6];
} rdas2000_beam_hdr_t;


  
